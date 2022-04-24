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
import app.simplecloud.simplecloud.database.api.DatabaseCloudProcessGroupRepository
import app.simplecloud.simplecloud.database.api.DatabaseOnlineCountStrategyRepository
import app.simplecloud.simplecloud.database.api.DatabasePermissionGroupRepository
import app.simplecloud.simplecloud.node.repository.distributed.DistributedOnlineCountStrategyRepository
import com.google.inject.Inject
import org.apache.logging.log4j.LogManager

class NodeRepositoriesInitializer @Inject constructor(
    private val distributedGroupRepository: DistributedCloudProcessGroupRepository,
    private val databaseCloudProcessGroupRepository: DatabaseCloudProcessGroupRepository,
    private val distributedPermissionGroupRepository: DistributedPermissionGroupRepository,
    private val databasePermissionGroupRepository: DatabasePermissionGroupRepository,
    private val distributedOnlineCountStrategyRepository: DistributedOnlineCountStrategyRepository,
    private val databaseOnlineCountStrategyRepository: DatabaseOnlineCountStrategyRepository
) {

    fun initializeRepositories() {
        logger.info("Initializing Distributed Repositories")
        initGroups()
        initPermissionGroups()
        initOnlineCountStrategies()
    }

    private fun initOnlineCountStrategies() {
        val configurations = this.databaseOnlineCountStrategyRepository.findAll().join()
        logger.info("Found OnlineCountStrategies: {}", configurations.map { it.name })
        for (config in configurations) {
            this.distributedOnlineCountStrategyRepository.save(config.name, config).join()
        }
    }

    private fun initPermissionGroups() {
        val configurations = this.databasePermissionGroupRepository.findAll().join()
        logger.info("Found PermissionGroups: {}", configurations.map { it.name })
        for (config in configurations) {
            this.distributedPermissionGroupRepository.save(config.name, config).join()
        }
    }

    private fun initGroups() {
        val configurations = this.databaseCloudProcessGroupRepository.findAll().join()
        logger.info("Found Groups: {}", configurations.map { it.name })
        for (config in configurations) {
            this.distributedGroupRepository.save(config.name, config).join()
        }
    }

    companion object {
        private val logger = LogManager.getLogger(NodeRepositoriesInitializer::class.java)
    }

}