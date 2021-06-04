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

package eu.thesimplecloud.simplecloud.api.impl.ignite

import com.google.inject.Inject
import com.google.inject.Singleton
import eu.thesimplecloud.simplecloud.api.impl.future.exception.CompletedWithNullException
import eu.thesimplecloud.simplecloud.api.impl.future.timeout.timout
import eu.thesimplecloud.simplecloud.api.impl.future.toFutureList
import eu.thesimplecloud.simplecloud.api.messagechannel.manager.IMessageChannelManager
import eu.thesimplecloud.simplecloud.api.repository.node.INodeRepository
import eu.thesimplecloud.simplecloud.api.repository.process.ICloudProcessRepository
import eu.thesimplecloud.simplecloud.api.utils.INetworkComponent
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
class IgniteQueryHandler @Inject constructor(
    private val messageChannelManager: IMessageChannelManager,
    private val nodeRepository: INodeRepository,
    private val processRepository: ICloudProcessRepository,
    private val ignite: Ignite
) {

    private val queries = CopyOnWriteArrayList<IgniteQuery>()

    init {
        startListening()
    }

    fun <T : Any> sendQuery(topic: String, message: Any, networkComponent: INetworkComponent): CompletableFuture<T> {
        val requestId = UUID.randomUUID()
        val future = createFutureWithTimeout<T>(600)
        createIgniteQuery(requestId, future)

        val transferObject = IgniteDataTransferObject(topic, requestId, message, false)
        sendPacket(transferObject, networkComponent.getIgniteId())
        return future
    }

    private fun <T> createFutureWithTimeout(timeout: Long): CompletableFuture<T> {
        val future = CompletableFuture<T>()
        future.timout(timeout)
        return future
    }

    private fun createIgniteQuery(requestId: UUID, future: CompletableFuture<*>): IgniteQuery {
        val igniteQuery = IgniteQuery(requestId, future as CompletableFuture<Any>)
        this.queries.add(igniteQuery)
        registerUnregisterListenerForQuery(future, igniteQuery)
        return igniteQuery
    }

    private fun sendPacket(transferObject: IgniteDataTransferObject, receiverNodeId: UUID) {
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
            handleQueryWithNetworkComponent(it, transferObject)
        }
    }

    private fun handleQueryWithNetworkComponent(
        networkComponent: INetworkComponent,
        transferObject: IgniteDataTransferObject
    ) {
        val channel = this.messageChannelManager.getMessageChannelByName<Any, Any>(transferObject.topic) ?: return
        if (transferObject.message == null) throw NullPointerException("Request object cannot be null")
        val result = channel.handleRequest(transferObject.message, networkComponent)
        sendPacket(
            IgniteDataTransferObject(transferObject.topic, transferObject.messageId, result, true),
            networkComponent.getIgniteId()
        )
    }

    private fun handleResponse(transferObject: IgniteDataTransferObject) {
        val igniteQuery = this.queries.firstOrNull { it.queryId == transferObject.messageId } ?: return
        if (transferObject.message == null) {
            igniteQuery.future.completeExceptionally(CompletedWithNullException())
        } else {
            igniteQuery.future.complete(transferObject.message)
        }
    }

    private fun getNetworkComponentByUniqueId(senderNodeId: UUID): CompletableFuture<out INetworkComponent> {
        val nodeFuture: CompletableFuture<out INetworkComponent> = nodeRepository.findNodeByUniqueId(senderNodeId)
        val processFuture: CompletableFuture<out INetworkComponent> =
            processRepository.findProcessByUniqueId(senderNodeId)
        return listOf(nodeFuture, processFuture).toFutureList().thenApply { it.first() }
    }

}