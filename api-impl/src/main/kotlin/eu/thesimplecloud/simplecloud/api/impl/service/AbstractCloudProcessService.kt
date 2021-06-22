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

import eu.thesimplecloud.simplecloud.api.impl.future.toFutureList
import eu.thesimplecloud.simplecloud.api.impl.repository.IgniteCloudProcessGroupRepository
import eu.thesimplecloud.simplecloud.api.impl.repository.IgniteCloudProcessRepository
import eu.thesimplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import eu.thesimplecloud.simplecloud.api.internal.service.IInternalCloudProcessService
import eu.thesimplecloud.simplecloud.api.process.ICloudProcess
import eu.thesimplecloud.simplecloud.api.process.group.ICloudProcessGroup
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 14.06.2021
 * Time: 13:31
 * @author Frederick Baier
 */
abstract class AbstractCloudProcessService(
    protected val igniteRepository: IgniteCloudProcessRepository
) : IInternalCloudProcessService {

    override fun findProcessByName(name: String): CompletableFuture<ICloudProcess> {
        return this.igniteRepository.find(name)
    }

    override fun findProcessesByName(vararg names: String): CompletableFuture<List<ICloudProcess>> {
        val futures = names.map { findProcessByName(it) }
        return futures.toFutureList()
    }

    override fun findProcessesByGroup(group: ICloudProcessGroup): CompletableFuture<List<ICloudProcess>> {
        return findProcessesByGroup(group.getName())
    }

    override fun findProcessesByGroup(groupName: String): CompletableFuture<List<ICloudProcess>> {
        return this.igniteRepository.findProcessesByGroupName(groupName)
    }

    override fun findProcessByUniqueId(uniqueId: UUID): CompletableFuture<ICloudProcess> {
        return this.igniteRepository.findProcessByUniqueId(uniqueId)
    }
}