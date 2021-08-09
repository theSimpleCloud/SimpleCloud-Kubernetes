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

import eu.thesimplecloud.simplecloud.api.future.completedFuture
import eu.thesimplecloud.simplecloud.api.impl.process.version.ProcessVersion
import eu.thesimplecloud.simplecloud.api.impl.repository.ignite.IgniteProcessVersionRepository
import eu.thesimplecloud.simplecloud.api.impl.request.processversion.ProcessVersionCreateRequest
import eu.thesimplecloud.simplecloud.api.impl.request.processversion.ProcessVersionDeleteRequest
import eu.thesimplecloud.simplecloud.api.internal.service.IInternalProcessVersionService
import eu.thesimplecloud.simplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.simplecloud.api.process.version.configuration.ProcessVersionConfiguration
import eu.thesimplecloud.simplecloud.api.request.processgroup.IProcessVersionCreateRequest
import eu.thesimplecloud.simplecloud.api.request.processgroup.IProcessVersionDeleteRequest
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 20.06.2021
 * Time: 10:36
 * @author Frederick Baier
 */
open class DefaultProcessVersionService(
    protected val igniteRepository: IgniteProcessVersionRepository
) : IInternalProcessVersionService {

    override fun findAll(): CompletableFuture<List<IProcessVersion>> {
        val completableFuture = this.igniteRepository.findAll()
        return completableFuture.thenApply { list -> list.map { ProcessVersion(it) } }
    }

    override fun findByName(name: String): CompletableFuture<IProcessVersion> {
        val completableFuture = this.igniteRepository.find(name)
        return completableFuture.thenApply { ProcessVersion(it) }
    }

    override fun doesExist(name: String): CompletableFuture<Boolean> {
        return this.igniteRepository.doesExist(name)
    }

    override fun createProcessVersionCreateRequest(configuration: ProcessVersionConfiguration): IProcessVersionCreateRequest {
        return ProcessVersionCreateRequest(this, configuration)
    }

    override fun createProcessVersionDeleteRequest(processVersion: IProcessVersion): IProcessVersionDeleteRequest {
        return ProcessVersionDeleteRequest(this, processVersion)
    }

    override fun createProcessVersionInternal(configuration: ProcessVersionConfiguration): CompletableFuture<IProcessVersion> {
        this.igniteRepository.save(configuration.name, configuration)
        return completedFuture(ProcessVersion(configuration))
    }

    override fun deleteProcessVersionInternal(processVersion: IProcessVersion) {
        this.igniteRepository.remove(processVersion.getIdentifier())
    }

}