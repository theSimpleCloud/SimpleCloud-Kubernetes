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
import eu.thesimplecloud.simplecloud.api.future.nonNull
import eu.thesimplecloud.simplecloud.api.future.toFutureList
import eu.thesimplecloud.simplecloud.api.impl.process.factory.ICloudProcessFactory
import eu.thesimplecloud.simplecloud.api.impl.request.process.ProcessShutdownRequest
import eu.thesimplecloud.simplecloud.api.impl.request.process.ProcessStartRequest
import eu.thesimplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import eu.thesimplecloud.simplecloud.api.internal.service.IInternalCloudProcessService
import eu.thesimplecloud.simplecloud.api.process.CloudProcessConfiguration
import eu.thesimplecloud.simplecloud.api.process.ICloudProcess
import eu.thesimplecloud.simplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.simplecloud.api.process.group.ProcessGroupType
import eu.thesimplecloud.simplecloud.api.process.state.ProcessState
import eu.thesimplecloud.simplecloud.api.request.process.IProcessShutdownRequest
import eu.thesimplecloud.simplecloud.api.request.process.IProcessStartRequest
import eu.thesimplecloud.simplecloud.api.utils.Address
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
    private val processFactory: ICloudProcessFactory
) : IInternalCloudProcessService {

    private val nameToProcess = ConcurrentHashMap<String, ICloudProcess>()

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

    override fun startNewProcessInternal(configuration: ProcessStartConfiguration): CompletableFuture<ICloudProcess> {
        return CompletableFuture.supplyAsync {
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
        }
    }

    override fun shutdownProcessInternal(process: ICloudProcess): CompletableFuture<Void> {
        this.nameToProcess.remove(process.getName())
        return CompletableFuture.completedFuture(null)
    }

    override fun findProcessByName(name: String): CompletableFuture<ICloudProcess> {
        return CompletableFuture.supplyAsync {
            this.nameToProcess[name] ?: throw NoSuchElementException("Process does not exist")
        }
    }

    override fun findProcessesByName(vararg names: String): CompletableFuture<List<ICloudProcess>> {
        return names.map { findProcessByName(it) }.toFutureList()
    }

    override fun findProcessesByGroup(group: ICloudProcessGroup): CompletableFuture<List<ICloudProcess>> {
        return findProcessesByGroup(group.getName())
    }

    override fun findProcessesByGroup(groupName: String): CompletableFuture<List<ICloudProcess>> {
        return CompletableFuture.supplyAsync {
            return@supplyAsync this.nameToProcess.values.filter { it.getGroupName() == groupName }
        }
    }

    override fun findProcessByUniqueId(uniqueId: UUID): CompletableFuture<ICloudProcess> {
        return CompletableFuture.supplyAsync {
            return@supplyAsync this.nameToProcess.values.firstOrNull { it.getUniqueId() == uniqueId }
        }.nonNull()
    }

    override fun createProcessStartRequest(group: ICloudProcessGroup): IProcessStartRequest {
        return ProcessStartRequest(this, group)
    }

    override fun createProcessShutdownRequest(group: ICloudProcess): IProcessShutdownRequest {
        return ProcessShutdownRequest(this, group)
    }

    override fun findAll(): CompletableFuture<List<ICloudProcess>> {
        return CompletableFuture.completedFuture(this.nameToProcess.values.toList())
    }


}