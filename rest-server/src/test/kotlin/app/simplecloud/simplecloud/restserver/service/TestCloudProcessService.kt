/*
 * SimpleCloud is a software for administrating a minecraft server network.
 * Copyright (C) 2022 Frederick Baier & Philipp Eistrach
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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