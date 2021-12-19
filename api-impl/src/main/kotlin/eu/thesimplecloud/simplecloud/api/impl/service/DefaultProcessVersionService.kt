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
import eu.thesimplecloud.simplecloud.api.impl.process.version.ProcessVersionImpl
import eu.thesimplecloud.simplecloud.api.impl.repository.ignite.IgniteProcessVersionRepository
import eu.thesimplecloud.simplecloud.api.impl.request.processversion.ProcessVersionCreateRequestImpl
import eu.thesimplecloud.simplecloud.api.impl.request.processversion.ProcessVersionDeleteRequestImpl
import eu.thesimplecloud.simplecloud.api.internal.service.InternalProcessVersionService
import eu.thesimplecloud.simplecloud.api.process.version.ProcessVersion
import eu.thesimplecloud.simplecloud.api.process.version.configuration.ProcessVersionConfiguration
import eu.thesimplecloud.simplecloud.api.request.processgroup.ProcessVersionCreateRequest
import eu.thesimplecloud.simplecloud.api.request.processgroup.ProcessVersionDeleteRequest
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 20.06.2021
 * Time: 10:36
 * @author Frederick Baier
 */
open class DefaultProcessVersionService(
    private val igniteRepository: IgniteProcessVersionRepository
) : InternalProcessVersionService {

    override fun findAll(): CompletableFuture<List<ProcessVersion>> {
        val completableFuture = this.igniteRepository.findAll()
        return completableFuture.thenApply { list -> list.map { ProcessVersionImpl(it) } }
    }

    override fun findByName(name: String): CompletableFuture<ProcessVersion> {
        val completableFuture = this.igniteRepository.find(name)
        return completableFuture.thenApply { ProcessVersionImpl(it) }
    }

    override fun doesExist(name: String): CompletableFuture<Boolean> {
        return this.igniteRepository.doesExist(name)
    }

    override fun createProcessVersionCreateRequest(configuration: ProcessVersionConfiguration): ProcessVersionCreateRequest {
        return ProcessVersionCreateRequestImpl(this, configuration)
    }

    override fun createProcessVersionDeleteRequest(processVersion: ProcessVersion): ProcessVersionDeleteRequest {
        return ProcessVersionDeleteRequestImpl(this, processVersion)
    }

    override fun createProcessVersionInternal(configuration: ProcessVersionConfiguration): CompletableFuture<ProcessVersion> {
        this.igniteRepository.save(configuration.name, configuration)
        return completedFuture(ProcessVersionImpl(configuration))
    }

    override fun deleteProcessVersionInternal(processVersion: ProcessVersion) {
        this.igniteRepository.remove(processVersion.getIdentifier())
    }

}