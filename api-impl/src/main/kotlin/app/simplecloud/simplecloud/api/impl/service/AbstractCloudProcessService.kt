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

package app.simplecloud.simplecloud.api.impl.service

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.future.toFutureList
import app.simplecloud.simplecloud.api.impl.process.factory.CloudProcessFactory
import app.simplecloud.simplecloud.api.impl.repository.ignite.IgniteCloudProcessRepository
import app.simplecloud.simplecloud.api.impl.request.process.ProcessShutdownRequestImpl
import app.simplecloud.simplecloud.api.impl.request.process.ProcessStartRequestImpl
import app.simplecloud.simplecloud.api.impl.request.process.ProcessUpdateRequestImpl
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessService
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.process.CloudProcessConfiguration
import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.request.process.ProcessShutdownRequest
import app.simplecloud.simplecloud.api.request.process.ProcessStartRequest
import app.simplecloud.simplecloud.api.request.process.ProcessUpdateRequest
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 14.06.2021
 * Time: 13:31
 * @author Frederick Baier
 */
abstract class AbstractCloudProcessService(
    protected val processFactory: CloudProcessFactory,
    protected val igniteRepository: IgniteCloudProcessRepository
) : InternalCloudProcessService {

    override fun findAll(): CompletableFuture<List<CloudProcess>> {
        val allProcessConfigurations = this.igniteRepository.findAll()
        return allProcessConfigurations.thenApply { configuration ->
            configuration.map { this.processFactory.create(it) }
        }
    }

    override fun findByName(name: String): CompletableFuture<CloudProcess> {
        val completableFuture = this.igniteRepository.find(name)
        return completableFuture.thenApply { this.processFactory.create(it) }
    }

    override fun findByNames(vararg names: String): CompletableFuture<List<CloudProcess>> {
        val futures = names.map { findByName(it) }
        return futures.toFutureList()
    }

    override fun findByGroup(group: CloudProcessGroup): CompletableFuture<List<CloudProcess>> {
        return findByGroup(group.getName())
    }

    override fun findByGroup(groupName: String): CompletableFuture<List<CloudProcess>> {
        val processesFuture = this.igniteRepository.findProcessesByGroupName(groupName)
        return processesFuture.thenApply { list -> list.map { this.processFactory.create(it) } }
    }

    override fun findByUniqueId(uniqueId: UUID): CompletableFuture<CloudProcess> {
        val completableFuture = this.igniteRepository.findProcessByUniqueId(uniqueId)
        return completableFuture.thenApply { this.processFactory.create(it) }
    }

    override fun findByIgniteId(igniteId: UUID): CompletableFuture<CloudProcess> {
        val completableFuture = this.igniteRepository.findProcessByIgniteId(igniteId)
        return completableFuture.thenApply { this.processFactory.create(it) }
    }

    override suspend fun updateProcessInternal(configuration: CloudProcessConfiguration) {
        this.igniteRepository.save(configuration.getProcessName(), configuration).await()
    }

    override fun createStartRequest(group: CloudProcessGroup): ProcessStartRequest {
        return ProcessStartRequestImpl(this, group)
    }

    override fun createUpdateRequest(process: CloudProcess): ProcessUpdateRequest {
        return ProcessUpdateRequestImpl(this, process)
    }

    override fun createShutdownRequest(process: CloudProcess): ProcessShutdownRequest {
        return ProcessShutdownRequestImpl(this, process)
    }

}