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
import eu.thesimplecloud.simplecloud.api.impl.jvmargs.JvmArgumentsImpl
import eu.thesimplecloud.simplecloud.api.impl.repository.ignite.IgniteJvmArgumentsRepository
import eu.thesimplecloud.simplecloud.api.impl.request.jvmargs.JvmArgumentCreateRequestImpl
import eu.thesimplecloud.simplecloud.api.impl.request.jvmargs.JvmArgumentDeleteRequestImpl
import eu.thesimplecloud.simplecloud.api.internal.service.InternalJvmArgumentsService
import eu.thesimplecloud.simplecloud.api.jvmargs.JVMArguments
import eu.thesimplecloud.simplecloud.api.jvmargs.configuration.JvmArgumentConfiguration
import eu.thesimplecloud.simplecloud.api.request.jvmargs.JvmArgumentCreateRequest
import eu.thesimplecloud.simplecloud.api.request.jvmargs.JvmArgumentDeleteRequest
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 14.06.2021
 * Time: 13:57
 * @author Frederick Baier
 */
open class DefaultJvmArgumentsService(
    private val igniteRepository: IgniteJvmArgumentsRepository
) : InternalJvmArgumentsService {

    override fun createJvmArgsInternal(configuration: JvmArgumentConfiguration): CompletableFuture<JVMArguments> {
        this.igniteRepository.save(configuration.name, configuration)
        return completedFuture(JvmArgumentsImpl(configuration))
    }

    override fun deleteJvmArgsInternal(jvmArgs: JVMArguments) {
        this.igniteRepository.remove(jvmArgs.getIdentifier())
    }

    override fun findByName(name: String): CompletableFuture<JVMArguments> {
        val completableFuture = this.igniteRepository.find(name)
        return completableFuture.thenApply { JvmArgumentsImpl(it) }
    }

    override fun findAll(): CompletableFuture<List<JVMArguments>> {
        return this.igniteRepository.findAll().thenApply { list -> list.map { JvmArgumentsImpl(it) } }
    }

    override fun doesExist(name: String): CompletableFuture<Boolean> {
        return this.igniteRepository.doesExist(name)
    }

    override fun createJvmArgumentsCreateRequest(configuration: JvmArgumentConfiguration): JvmArgumentCreateRequest {
        return JvmArgumentCreateRequestImpl(this, configuration)
    }

    override fun createJvmArgumentsDeleteRequest(jvmArgs: JVMArguments): JvmArgumentDeleteRequest {
        return JvmArgumentDeleteRequestImpl(this, jvmArgs)
    }

}