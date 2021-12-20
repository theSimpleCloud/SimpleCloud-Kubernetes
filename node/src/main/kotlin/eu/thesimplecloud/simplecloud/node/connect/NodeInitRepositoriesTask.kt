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

package eu.thesimplecloud.simplecloud.node.connect

import com.ea.async.Async.await
import com.google.inject.Inject
import eu.thesimplecloud.simplecloud.api.future.unitFuture
import eu.thesimplecloud.simplecloud.api.impl.repository.ignite.IgniteCloudProcessGroupRepository
import eu.thesimplecloud.simplecloud.api.impl.repository.ignite.IgniteJvmArgumentsRepository
import eu.thesimplecloud.simplecloud.api.impl.repository.ignite.IgniteProcessVersionRepository
import eu.thesimplecloud.simplecloud.api.jvmargs.configuration.JvmArgumentConfiguration
import eu.thesimplecloud.simplecloud.api.process.version.configuration.ProcessVersionConfiguration
import eu.thesimplecloud.simplecloud.node.mongo.group.MongoCloudProcessGroupRepository
import eu.thesimplecloud.simplecloud.node.mongo.jvmargs.MongoJvmArgumentsRepository
import eu.thesimplecloud.simplecloud.node.mongo.processversion.MongoProcessVersionRepository
import java.util.concurrent.CompletableFuture

class NodeInitRepositoriesTask @Inject constructor(
    private val igniteGroupRepository: IgniteCloudProcessGroupRepository,
    private val mongoCloudProcessGroupRepository: MongoCloudProcessGroupRepository,
    private val igniteProcessVersionRepository: IgniteProcessVersionRepository,
    private val mongoProcessVersionRepository: MongoProcessVersionRepository,
    private val igniteJvmArgumentsRepository: IgniteJvmArgumentsRepository,
    private val mongoJvmArgumentsRepository: MongoJvmArgumentsRepository,
) {

    fun run(): CompletableFuture<Unit> {
        await(initJvmArguments())
        await(initProcessVersions())
        await(initGroups())
        return unitFuture()
    }

    private fun initJvmArguments(): CompletableFuture<Unit> {
        val jvmArgs = await(this.mongoJvmArgumentsRepository.findAll())
        val configurations = jvmArgs.map { JvmArgumentConfiguration(it.name, it.arguments) }
        for (config in configurations) {
            await(this.igniteJvmArgumentsRepository.save(config.name, config))
        }
        return unitFuture()
    }

    private fun initProcessVersions(): CompletableFuture<Unit> {
        val versions = await(this.mongoProcessVersionRepository.findAll())
        val configurations = versions.map {
            ProcessVersionConfiguration(it.name, it.apiType, it.loadType, it.downloadLink, it.javaBaseImageName)
        }
        for (config in configurations) {
            await(this.igniteProcessVersionRepository.save(config.name, config))
        }
        return unitFuture()
    }

    private fun initGroups(): CompletableFuture<Unit> {
        val groups = await(this.mongoCloudProcessGroupRepository.findAll())
        val groupConfigurations = groups.map { it.toConfiguration() }
        for (config in groupConfigurations) {
            await(this.igniteGroupRepository.save(config.name, config))
        }
        return unitFuture()
    }
}