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

package app.simplecloud.simplecloud.node.connect

import app.simplecloud.simplecloud.api.future.unitFuture
import app.simplecloud.simplecloud.api.impl.repository.ignite.IgniteCloudProcessGroupRepository
import app.simplecloud.simplecloud.node.mongo.group.MongoCloudProcessGroupRepository
import com.ea.async.Async.await
import com.google.inject.Inject
import org.apache.logging.log4j.LogManager
import java.util.concurrent.CompletableFuture

class NodeInitRepositoriesTask @Inject constructor(
    private val igniteGroupRepository: IgniteCloudProcessGroupRepository,
    private val mongoCloudProcessGroupRepository: MongoCloudProcessGroupRepository,
) {

    fun run(): CompletableFuture<Unit> {
        logger.info("Initializing Ignite Repositories")
        await(initGroups())
        return unitFuture()
    }

    private fun initGroups(): CompletableFuture<Unit> {
        val groups = await(this.mongoCloudProcessGroupRepository.findAll())
        logger.info("Found Groups: {}", groups.map { it.name })
        val groupConfigurations = groups.map { it.toConfiguration() }
        for (config in groupConfigurations) {
            await(this.igniteGroupRepository.save(config.name, config))
        }
        return unitFuture()
    }

    companion object {
        private val logger = LogManager.getLogger(NodeInitRepositoriesTask::class.java)
    }

}