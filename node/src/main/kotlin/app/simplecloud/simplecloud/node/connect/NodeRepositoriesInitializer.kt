/*
 * SimpleCloud is a software for administrating a minecraft server network.
 * Copyright (C) 2022 Frederick Baier & Philipp Eistrach
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package app.simplecloud.simplecloud.node.connect

import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedCloudProcessGroupRepository
import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedPermissionGroupRepository
import app.simplecloud.simplecloud.node.repository.distributed.DistributedOnlineCountStrategyRepository
import app.simplecloud.simplecloud.node.repository.mongo.group.MongoCloudProcessGroupRepository
import app.simplecloud.simplecloud.node.repository.mongo.onlinecountstrategy.MongoOnlineCountStrategyRepository
import app.simplecloud.simplecloud.node.repository.mongo.permission.MongoPermissionGroupRepository
import com.google.inject.Inject
import org.apache.logging.log4j.LogManager

class NodeRepositoriesInitializer @Inject constructor(
    private val distributedGroupRepository: DistributedCloudProcessGroupRepository,
    private val mongoCloudProcessGroupRepository: MongoCloudProcessGroupRepository,
    private val distributedPermissionGroupRepository: DistributedPermissionGroupRepository,
    private val mongoPermissionGroupRepository: MongoPermissionGroupRepository,
    private val distributedOnlineCountStrategyRepository: DistributedOnlineCountStrategyRepository,
    private val mongoOnlineCountStrategyRepository: MongoOnlineCountStrategyRepository
) {

    fun initializeRepositories() {
        logger.info("Initializing Ignite Repositories")
        initGroups()
        initPermissionGroups()
        initOnlineCountStrategies()
    }

    private fun initOnlineCountStrategies() {
        val entities = this.mongoOnlineCountStrategyRepository.findAll().join()
        logger.info("Found OnlineCountStrategies: {}", entities.map { it.name })
        val configurations = entities.map { it.toConfiguration() }
        for (config in configurations) {
            this.distributedOnlineCountStrategyRepository.save(config.name, config).join()
        }
    }

    private fun initPermissionGroups() {
        val groups = this.mongoPermissionGroupRepository.findAll().join()
        logger.info("Found PermissionGroups: {}", groups.map { it.name })
        val configurations = groups.map { it.toConfiguration() }
        for (config in configurations) {
            this.distributedPermissionGroupRepository.save(config.name, config).join()
        }
    }

    private fun initGroups() {
        val groups = this.mongoCloudProcessGroupRepository.findAll().join()
        logger.info("Found Groups: {}", groups.map { it.name })
        val groupConfigurations = groups.map { it.toConfiguration() }
        for (config in groupConfigurations) {
            this.distributedGroupRepository.save(config.name, config).join()
        }
    }

    companion object {
        private val logger = LogManager.getLogger(NodeRepositoriesInitializer::class.java)
    }

}