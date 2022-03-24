/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package app.simplecloud.simplecloud.api.impl.ignite

import app.simplecloud.simplecloud.api.future.CloudCompletableFuture
import app.simplecloud.simplecloud.api.future.timeout.timout
import app.simplecloud.simplecloud.api.future.toFutureList
import app.simplecloud.simplecloud.api.messagechannel.manager.MessageChannelManager
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.api.service.NodeService
import app.simplecloud.simplecloud.api.utils.NetworkComponent
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.Singleton
import org.apache.ignite.Ignite
import org.apache.ignite.lang.IgniteBiPredicate
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by IntelliJ IDEA.
 * Date: 29.05.2021
 * Time: 18:30
 * @author Frederick Baier
 */
@Singleton
class IgniteQueryHandlerImpl @Inject constructor(
    private val messageChannelManager: MessageChannelManager,
    private val injector: Injector,
    private val ignite: Ignite
) : IgniteQueryHandler {

    private val queries = CopyOnWriteArrayList<IgniteQuery>()

    private lateinit var nodeService: NodeService
    private lateinit var processService: CloudProcessService

    init {
        startListening()
    }

    override fun <T> sendQuery(topic: String, message: Any, networkComponent: NetworkComponent): CompletableFuture<T> {
        val requestId = UUID.randomUUID()
        val future = createFutureWithTimeout<T>(1000)
        createIgniteQuery(requestId, future)

        val transferObject = IgniteDataTransferObject(topic, requestId, Result.success(message), false)
        sendPacket(transferObject, networkComponent.getIgniteId())
        return future
    }

    private fun <T> createFutureWithTimeout(timeout: Long): CompletableFuture<T> {
        val future = CloudCompletableFuture<T>()
        future.timout(timeout)
        return future
    }

    private fun createIgniteQuery(requestId: UUID, future: CompletableFuture<*>): IgniteQuery {
        val igniteQuery = IgniteQuery(requestId, future as CompletableFuture<Any>)
        this.queries.add(igniteQuery)
        registerUnregisterListenerForQuery(future, igniteQuery)
        return igniteQuery
    }

    fun sendPacket(transferObject: IgniteDataTransferObject, receiverNodeId: UUID) {
        val clusterGroup = ignite.cluster().forNodeId(receiverNodeId)
        ignite.message(clusterGroup).send("cloud-topic", transferObject)
    }

    private fun <T> registerUnregisterListenerForQuery(future: CompletableFuture<T>, igniteQuery: IgniteQuery) {
        future.handle { _, _ ->
            this.queries.remove(igniteQuery)
        }
    }

    private fun startListening() {
        ignite.message().localListen("cloud-topic", IgniteBiPredicate<UUID, IgniteDataTransferObject> { uuid, data ->
            handleMessage(uuid, data)
            return@IgniteBiPredicate true
        })
    }

    private fun handleMessage(senderNodeId: UUID, transferObject: IgniteDataTransferObject) {
        if (transferObject.isResponse) {
            handleResponse(transferObject)
        } else {
            handleQuery(senderNodeId, transferObject)
        }
    }

    private fun handleQuery(senderNodeId: UUID, transferObject: IgniteDataTransferObject) {
        val networkComponent = getNetworkComponentByUniqueId(senderNodeId)
        networkComponent.thenAccept {
            IgniteIncomingQueryHandler(this, messageChannelManager, it, transferObject).handle()
        }.exceptionally {
            it.printStackTrace()
            return@exceptionally null
        }
    }

    private fun handleResponse(transferObject: IgniteDataTransferObject) {
        val igniteQuery = getIgniteQueryByMessageId(transferObject.messageId) ?: return
        if (transferObject.message.isSuccess) {
            igniteQuery.future.complete(transferObject.message.getOrThrow())
        } else {
            igniteQuery.future.completeExceptionally(transferObject.message.exceptionOrNull())
        }
    }

    private fun getIgniteQueryByMessageId(messageId: UUID): IgniteQuery? {
        return this.queries.firstOrNull { it.queryId == messageId }
    }

    private fun getNetworkComponentByUniqueId(senderNodeId: UUID): CompletableFuture<out NetworkComponent> {
        checkServicesInitialized()
        val nodeFuture: CompletableFuture<out NetworkComponent> = this.nodeService.findByUniqueId(senderNodeId)
        val processFuture: CompletableFuture<out NetworkComponent> =
            this.processService.findByIgniteId(senderNodeId)
        val futureList = listOf(nodeFuture, processFuture).toFutureList()
        return futureList.thenApply { it.first() }
            .exceptionally { throw NoSuchElementException("Could not find NetworkComponent by id: $senderNodeId") }
    }

    private fun checkServicesInitialized() {
        if (!areServicesInitialized())
            initializeServices()
    }

    private fun areServicesInitialized(): Boolean {
        return this::nodeService.isInitialized && this::processService.isInitialized
    }

    private fun initializeServices() {
        this.nodeService = this.injector.getInstance(NodeService::class.java)
        this.processService = this.injector.getInstance(CloudProcessService::class.java)
    }


}