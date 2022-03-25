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
import app.simplecloud.simplecloud.api.impl.repository.ignite.IgnitePermissionGroupRepository
import app.simplecloud.simplecloud.node.repository.ignite.IgniteOnlineCountStrategyMapRepository
import app.simplecloud.simplecloud.node.repository.mongo.group.MongoCloudProcessGroupRepository
import app.simplecloud.simplecloud.node.repository.mongo.permission.MongoPermissionGroupRepository
import app.simplecloud.simplecloud.node.repository.mongo.strategymap.MongoOnlineCountStrategyMapRepository
import com.google.inject.Inject
import org.apache.logging.log4j.LogManager
import java.util.concurrent.CompletableFuture

class NodeRepositoriesInitializer @Inject constructor(
    private val igniteGroupRepository: IgniteCloudProcessGroupRepository,
    private val mongoCloudProcessGroupRepository: MongoCloudProcessGroupRepository,
    private val ignitePermissionGroupRepository: IgnitePermissionGroupRepository,
    private val mongoPermissionGroupRepository: MongoPermissionGroupRepository,
    private val igniteOnlineCountStrategyMapRepository: IgniteOnlineCountStrategyMapRepository,
    private val mongoOnlineCountStrategyMapRepository: MongoOnlineCountStrategyMapRepository
) {

    fun initializeRepositories() {
        logger.info("Initializing Ignite Repositories")
        initGroups()
        initPermissionGroups()
        initOnlineCountStrategyMap()
    }

    private fun initOnlineCountStrategyMap() {
        val entities = this.mongoOnlineCountStrategyMapRepository.findAll().join()
        logger.info("Found OnlineCountStrategy Mappings: {}", entities.map { it.groupName + " to " + it.onlineStrategyName })
        for (entity in entities) {
            this.igniteOnlineCountStrategyMapRepository.save(entity.groupName, entity.onlineStrategyName).join()
        }
    }

    private fun initPermissionGroups(): CompletableFuture<Unit> {
        val groups = this.mongoPermissionGroupRepository.findAll().join()
        logger.info("Found PermissionGroups: {}", groups.map { it.name })
        val configurations = groups.map { it.toConfiguration() }
        for (config in configurations) {
            this.ignitePermissionGroupRepository.save(config.name, config).join()
        }
        return unitFuture()
    }

    private fun initGroups(){
        val groups = this.mongoCloudProcessGroupRepository.findAll().join()
        logger.info("Found Groups: {}", groups.map { it.name })
        val groupConfigurations = groups.map { it.toConfiguration() }
        for (config in groupConfigurations) {
            this.igniteGroupRepository.save(config.name, config).join()
        }
    }

    companion object {
        private val logger = LogManager.getLogger(NodeRepositoriesInitializer::class.java)
    }

}