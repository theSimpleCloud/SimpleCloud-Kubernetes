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
import eu.thesimplecloud.simplecloud.api.impl.jvmargs.JvmArguments
import eu.thesimplecloud.simplecloud.api.impl.request.jvmargs.JvmArgumentCreateRequest
import eu.thesimplecloud.simplecloud.api.impl.request.jvmargs.JvmArgumentDeleteRequest
import eu.thesimplecloud.simplecloud.api.internal.service.IInternalJvmArgumentsService
import eu.thesimplecloud.simplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.simplecloud.api.jvmargs.configuration.JvmArgumentConfiguration
import eu.thesimplecloud.simplecloud.api.request.jvmargs.IJvmArgumentCreateRequest
import eu.thesimplecloud.simplecloud.api.request.jvmargs.IJvmArgumentDeleteRequest
import java.util.NoSuchElementException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by IntelliJ IDEA.
 * Date: 03/07/2021
 * Time: 18:59
 * @author Frederick Baier
 */
@Singleton
class TestJvmArgumentsService : IInternalJvmArgumentsService {

    private val nameToJvmArgs = ConcurrentHashMap<String, IJVMArguments>()

    init {
        this.nameToJvmArgs["Test"] = JvmArguments(
            JvmArgumentConfiguration("Test", emptyList())
        )
    }

    override fun createJvmArgsInternal(configuration: JvmArgumentConfiguration): CompletableFuture<IJVMArguments> {
        val jvmArguments = JvmArguments(configuration)
        this.nameToJvmArgs[jvmArguments.getIdentifier()] = jvmArguments
        return completedFuture(jvmArguments)
    }

    override fun deleteJvmArgsInternal(jvmArgs: IJVMArguments) {
        this.nameToJvmArgs.remove(jvmArgs.getIdentifier())
    }

    override fun findByName(name: String): CompletableFuture<IJVMArguments> {
        return CompletableFuture.supplyAsync {
            this.nameToJvmArgs[name] ?: throw NoSuchElementException("JvmArgs '${name}' does not exist")
        }
    }

    override fun findAll(): CompletableFuture<List<IJVMArguments>> {
        return completedFuture(this.nameToJvmArgs.values.toList())
    }

    override fun doesExist(name: String): CompletableFuture<Boolean> {
        return completedFuture(this.nameToJvmArgs.containsKey(name))
    }

    override fun createJvmArgumentsCreateRequest(configuration: JvmArgumentConfiguration): IJvmArgumentCreateRequest {
        return JvmArgumentCreateRequest(this, configuration)
    }

    override fun createJvmArgumentsDeleteRequest(jvmArgs: IJVMArguments): IJvmArgumentDeleteRequest {
        return JvmArgumentDeleteRequest(this, jvmArgs)
    }
}