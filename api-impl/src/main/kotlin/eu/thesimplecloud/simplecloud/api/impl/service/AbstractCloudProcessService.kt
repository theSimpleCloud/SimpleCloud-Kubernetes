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

package eu.thesimplecloud.simplecloud.api.impl.service

import eu.thesimplecloud.simplecloud.api.future.toFutureList
import eu.thesimplecloud.simplecloud.api.impl.process.factory.CloudProcessFactory
import eu.thesimplecloud.simplecloud.api.impl.repository.ignite.IgniteCloudProcessRepository
import eu.thesimplecloud.simplecloud.api.impl.request.process.ProcessShutdownRequestImpl
import eu.thesimplecloud.simplecloud.api.impl.request.process.ProcessStartRequestImpl
import eu.thesimplecloud.simplecloud.api.internal.service.InternalCloudProcessService
import eu.thesimplecloud.simplecloud.api.process.CloudProcess
import eu.thesimplecloud.simplecloud.api.process.group.CloudProcessGroup
import eu.thesimplecloud.simplecloud.api.request.process.ProcessStartRequest
import eu.thesimplecloud.simplecloud.api.request.process.ProcessShutdownRequest
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

    override fun findProcessByName(name: String): CompletableFuture<CloudProcess> {
        val completableFuture = this.igniteRepository.find(name)
        return completableFuture.thenApply { this.processFactory.create(it) }
    }

    override fun findProcessesByName(vararg names: String): CompletableFuture<List<CloudProcess>> {
        val futures = names.map { findProcessByName(it) }
        return futures.toFutureList()
    }

    override fun findProcessesByGroup(group: CloudProcessGroup): CompletableFuture<List<CloudProcess>> {
        return findProcessesByGroup(group.getName())
    }

    override fun findProcessesByGroup(groupName: String): CompletableFuture<List<CloudProcess>> {
        val processesFuture = this.igniteRepository.findProcessesByGroupName(groupName)
        return processesFuture.thenApply { list -> list.map { this.processFactory.create(it) } }
    }

    override fun findProcessByUniqueId(uniqueId: UUID): CompletableFuture<CloudProcess> {
        val completableFuture = this.igniteRepository.findProcessByUniqueId(uniqueId)
        return completableFuture.thenApply { this.processFactory.create(it) }
    }

    override fun createProcessStartRequest(group: CloudProcessGroup): ProcessStartRequest {
        return ProcessStartRequestImpl(this, group)
    }

    override fun createProcessShutdownRequest(group: CloudProcess): ProcessShutdownRequest {
        return ProcessShutdownRequestImpl(this, group)
    }

}