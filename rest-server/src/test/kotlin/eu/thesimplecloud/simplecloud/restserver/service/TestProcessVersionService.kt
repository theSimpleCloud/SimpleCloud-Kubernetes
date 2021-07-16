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

import com.google.inject.Singleton
import eu.thesimplecloud.simplecloud.api.future.completedFuture
import eu.thesimplecloud.simplecloud.api.impl.process.version.ProcessVersion
import eu.thesimplecloud.simplecloud.api.impl.request.processversion.ProcessVersionCreateRequest
import eu.thesimplecloud.simplecloud.api.impl.request.processversion.ProcessVersionDeleteRequest
import eu.thesimplecloud.simplecloud.api.internal.service.IInternalProcessVersionService
import eu.thesimplecloud.simplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.simplecloud.api.process.version.ProcessAPIType
import eu.thesimplecloud.simplecloud.api.process.version.configuration.ProcessVersionConfiguration
import eu.thesimplecloud.simplecloud.api.request.processgroup.IProcessVersionCreateRequest
import eu.thesimplecloud.simplecloud.api.request.processgroup.IProcessVersionDeleteRequest
import java.util.NoSuchElementException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by IntelliJ IDEA.
 * Date: 03/07/2021
 * Time: 19:09
 * @author Frederick Baier
 */
@Singleton
class TestProcessVersionService : IInternalProcessVersionService {

    private val nameToProcessVersion = ConcurrentHashMap<String, IProcessVersion>()

    init {
        this.nameToProcessVersion["Lobby"] = ProcessVersion(
            ProcessVersionConfiguration("Test", ProcessAPIType.SPIGOT, "https://test.net")
        )
    }

    override fun findByName(name: String): CompletableFuture<IProcessVersion> {
        return CompletableFuture.supplyAsync {
            this.nameToProcessVersion[name] ?: throw NoSuchElementException("ProcessVersion '${name}' does not exist")
        }
    }

    override fun doesExist(name: String): CompletableFuture<Boolean> {
        return completedFuture(this.nameToProcessVersion.containsKey(name))
    }

    override fun findAll(): CompletableFuture<List<IProcessVersion>> {
        return CompletableFuture.completedFuture(this.nameToProcessVersion.values.toList())
    }

    override fun createProcessVersionInternal(configuration: ProcessVersionConfiguration): CompletableFuture<IProcessVersion> {
        val processVersion = ProcessVersion(configuration)
        this.nameToProcessVersion[processVersion.getName()] = processVersion
        return CompletableFuture.completedFuture(processVersion)
    }

    override fun deleteProcessVersionInternal(processVersion: IProcessVersion) {
        this.nameToProcessVersion.remove(processVersion.getName())
    }

    override fun createProcessVersionCreateRequest(configuration: ProcessVersionConfiguration): IProcessVersionCreateRequest {
        return ProcessVersionCreateRequest(this, configuration)
    }

    override fun createProcessVersionDeleteRequest(processVersion: IProcessVersion): IProcessVersionDeleteRequest {
        return ProcessVersionDeleteRequest(this, processVersion)
    }

}