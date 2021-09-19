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

package eu.thesimplecloud.simplecloud.node.service

import com.google.inject.Inject
import com.google.inject.Singleton
import eu.thesimplecloud.simplecloud.api.impl.repository.ignite.IgniteJvmArgumentsRepository
import eu.thesimplecloud.simplecloud.api.impl.service.DefaultJvmArgumentsService
import eu.thesimplecloud.simplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.simplecloud.api.jvmargs.configuration.JvmArgumentConfiguration
import eu.thesimplecloud.simplecloud.node.mongo.jvmargs.JvmArgumentsEntity
import eu.thesimplecloud.simplecloud.node.mongo.jvmargs.MongoJvmArgumentsRepository
import java.util.concurrent.CompletableFuture

@Singleton
class JvmArgumentsServiceImpl @Inject constructor(
    igniteRepository: IgniteJvmArgumentsRepository,
    private val mongoRepository: MongoJvmArgumentsRepository
) : DefaultJvmArgumentsService(
    igniteRepository
) {

    override fun createJvmArgsInternal(configuration: JvmArgumentConfiguration): CompletableFuture<IJVMArguments> {
        val result = super.createJvmArgsInternal(configuration)
        saveToDatabase(configuration)
        return result
    }

    private fun saveToDatabase(configuration: JvmArgumentConfiguration) {
        val entity = JvmArgumentsEntity(configuration.name, configuration.arguments)
        this.mongoRepository.save(configuration.name, entity)
    }

    override fun deleteJvmArgsInternal(jvmArgs: IJVMArguments) {
        super.deleteJvmArgsInternal(jvmArgs)
        this.mongoRepository.remove(jvmArgs.getName())
    }

}