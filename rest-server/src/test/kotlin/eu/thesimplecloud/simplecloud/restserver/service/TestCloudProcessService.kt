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

package eu.thesimplecloud.simplecloud.restserver.service

import com.google.inject.Inject
import com.google.inject.Singleton
import eu.thesimplecloud.simplecloud.api.future.cloud.nonNull
import eu.thesimplecloud.simplecloud.api.future.toFutureList
import eu.thesimplecloud.simplecloud.api.future.unitFuture
import eu.thesimplecloud.simplecloud.api.impl.process.factory.CloudProcessFactory
import eu.thesimplecloud.simplecloud.api.impl.request.process.ProcessShutdownRequestImpl
import eu.thesimplecloud.simplecloud.api.impl.request.process.ProcessStartRequestImpl
import eu.thesimplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import eu.thesimplecloud.simplecloud.api.internal.service.InternalCloudProcessService
import eu.thesimplecloud.simplecloud.api.process.CloudProcessConfiguration
import eu.thesimplecloud.simplecloud.api.process.CloudProcess
import eu.thesimplecloud.simplecloud.api.process.group.CloudProcessGroup
import eu.thesimplecloud.simplecloud.api.process.group.ProcessGroupType
import eu.thesimplecloud.simplecloud.api.process.state.ProcessState
import eu.thesimplecloud.simplecloud.api.request.process.ProcessShutdownRequest
import eu.thesimplecloud.simplecloud.api.request.process.ProcessStartRequest
import eu.thesimplecloud.simplecloud.api.utils.Address
import eu.thesimplecloud.simplecloud.api.utils.future.CloudCompletableFuture
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
                ProcessState.VISIBLE,
                512,
                512,
                20,
                Address.fromIpString("127.0.0.1:11111"),
                false,
                ProcessGroupType.PROXY,
                "TEST",
                "Test",
                "Test",
                null,
                null
            )
        )
    }

    override fun startNewProcessInternal(configuration: ProcessStartConfiguration): CompletableFuture<CloudProcess> {
        return CloudCompletableFuture.supplyAsync {
            val process = this.processFactory.create(
                CloudProcessConfiguration(
                    configuration.groupName,
                    UUID.randomUUID(),
                    Random.nextInt(20),
                    ProcessState.STARTING,
                    configuration.maxMemory,
                    0,
                    configuration.maxPlayers,
                    Address.fromIpString("127.0.0.1:25565"),
                    false,
                    ProcessGroupType.LOBBY,
                    configuration.processVersionName,
                    configuration.templateName,
                    "Node-1",
                    configuration.jvmArgumentsName,
                    null
                )
            )
            this.nameToProcess[process.getName()] = process
            process
        }.nonNull()
    }

    override fun shutdownProcessInternal(process: CloudProcess): CompletableFuture<Unit> {
        this.nameToProcess.remove(process.getName())
        return unitFuture()
    }

    override fun findProcessByName(name: String): CompletableFuture<CloudProcess> {
        return CloudCompletableFuture.supplyAsync {
            this.nameToProcess[name] ?: throw NoSuchElementException("Process does not exist")
        }.nonNull()
    }

    override fun findProcessesByName(vararg names: String): CompletableFuture<List<CloudProcess>> {
        return names.map { findProcessByName(it) }.toFutureList()
    }

    override fun findProcessesByGroup(group: CloudProcessGroup): CompletableFuture<List<CloudProcess>> {
        return findProcessesByGroup(group.getName())
    }

    override fun findProcessesByGroup(groupName: String): CompletableFuture<List<CloudProcess>> {
        return CloudCompletableFuture.supplyAsync {
            return@supplyAsync this.nameToProcess.values.filter { it.getGroupName() == groupName }
        }.nonNull()
    }

    override fun findProcessByUniqueId(uniqueId: UUID): CompletableFuture<CloudProcess> {
        return CloudCompletableFuture.supplyAsync {
            return@supplyAsync this.nameToProcess.values.firstOrNull { it.getUniqueId() == uniqueId }
        }.nonNull()
    }

    override fun createProcessStartRequest(group: CloudProcessGroup): ProcessStartRequest {
        return ProcessStartRequestImpl(this, group)
    }

    override fun createProcessShutdownRequest(group: CloudProcess): ProcessShutdownRequest {
        return ProcessShutdownRequestImpl(this, group)
    }

    override fun findAll(): CompletableFuture<List<CloudProcess>> {
        return CloudCompletableFuture.completedFuture(this.nameToProcess.values.toList())
    }


}