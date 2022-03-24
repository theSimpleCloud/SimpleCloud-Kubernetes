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

package app.simplecloud.simplecloud.restserver.service

import app.simplecloud.simplecloud.api.future.CloudCompletableFuture
import app.simplecloud.simplecloud.api.future.nonNull
import app.simplecloud.simplecloud.api.future.toFutureList
import app.simplecloud.simplecloud.api.impl.process.factory.CloudProcessFactory
import app.simplecloud.simplecloud.api.impl.request.process.ProcessShutdownRequestImpl
import app.simplecloud.simplecloud.api.impl.request.process.ProcessStartRequestImpl
import app.simplecloud.simplecloud.api.impl.request.process.ProcessUpdateRequestImpl
import app.simplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessService
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.process.CloudProcessConfiguration
import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.process.group.ProcessGroupType
import app.simplecloud.simplecloud.api.process.state.ProcessState
import app.simplecloud.simplecloud.api.request.process.ProcessShutdownRequest
import app.simplecloud.simplecloud.api.request.process.ProcessStartRequest
import app.simplecloud.simplecloud.api.request.process.ProcessUpdateRequest
import com.google.inject.Inject
import com.google.inject.Singleton
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

/**
 * Created by IntelliJ IDEA.
 * Date: 03/07/2021
 * Time: 19:07
 * @author Frederick Baier
 */
@Singleton
class TestCloudProcessService @Inject constructor(
    private val processFactory: CloudProcessFactory
) : InternalCloudProcessService {

    private val nameToProcess = ConcurrentHashMap<String, CloudProcess>()

    init {
        this.nameToProcess["Lobby-1"] = processFactory.create(
            CloudProcessConfiguration(
                "Lobby",
                UUID.randomUUID(),
                1,
                ProcessState.ONLINE,
                true,
                512,
                512,
                20,
                5,
                false,
                ProcessGroupType.PROXY,
                "TEST",
                null,
            )
        )
    }

    override suspend fun startNewProcessInternal(configuration: ProcessStartConfiguration): CloudProcess {
        val process = this.processFactory.create(
            CloudProcessConfiguration(
                configuration.groupName,
                UUID.randomUUID(),
                Random.nextInt(20),
                ProcessState.STARTING,
                true,
                configuration.maxMemory,
                0,
                configuration.maxPlayers,
                0,
                false,
                ProcessGroupType.LOBBY,
                configuration.imageName,
                null
            )
        )
        this.nameToProcess[process.getName()] = process
        return process
    }

    override suspend fun shutdownProcessInternal(process: CloudProcess) {
        this.nameToProcess.remove(process.getName())
    }

    override suspend fun updateProcessInternal(configuration: CloudProcessConfiguration) {
        TODO("Not yet implemented")
    }

    override fun findByName(name: String): CompletableFuture<CloudProcess> {
        return CloudCompletableFuture.supplyAsync {
            this.nameToProcess[name] ?: throw NoSuchElementException("Process does not exist")
        }.nonNull()
    }

    override fun findByNames(vararg names: String): CompletableFuture<List<CloudProcess>> {
        return names.map { findByName(it) }.toFutureList()
    }

    override fun findByGroup(group: CloudProcessGroup): CompletableFuture<List<CloudProcess>> {
        return findByGroup(group.getName())
    }

    override fun findByGroup(groupName: String): CompletableFuture<List<CloudProcess>> {
        return CloudCompletableFuture.supplyAsync {
            return@supplyAsync this.nameToProcess.values.filter { it.getGroupName() == groupName }
        }.nonNull()
    }

    override fun findByUniqueId(uniqueId: UUID): CompletableFuture<CloudProcess> {
        return CloudCompletableFuture.supplyAsync {
            return@supplyAsync this.nameToProcess.values.firstOrNull { it.getUniqueId() == uniqueId }
        }.nonNull()
    }

    override fun findByIgniteId(igniteId: UUID): CompletableFuture<CloudProcess> {
        return CloudCompletableFuture.supplyAsync {
            return@supplyAsync this.nameToProcess.values.firstOrNull { it.getIgniteId() == igniteId }
        }.nonNull()
    }

    override fun createStartRequest(group: CloudProcessGroup): ProcessStartRequest {
        return ProcessStartRequestImpl(this, group)
    }

    override fun createShutdownRequest(group: CloudProcess): ProcessShutdownRequest {
        return ProcessShutdownRequestImpl(this, group)
    }

    override fun createUpdateRequest(process: CloudProcess): ProcessUpdateRequest {
        return ProcessUpdateRequestImpl(this, process)
    }

    override fun findAll(): CompletableFuture<List<CloudProcess>> {
        return CloudCompletableFuture.completedFuture(this.nameToProcess.values.toList())
    }


}