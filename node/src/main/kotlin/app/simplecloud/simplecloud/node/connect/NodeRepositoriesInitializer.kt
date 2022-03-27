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

import app.simplecloud.simplecloud.api.impl.repository.ignite.IgniteCloudProcessGroupRepository
import app.simplecloud.simplecloud.api.impl.repository.ignite.IgnitePermissionGroupRepository
import app.simplecloud.simplecloud.node.repository.ignite.IgniteOnlineCountStrategyRepository
import app.simplecloud.simplecloud.node.repository.mongo.group.MongoCloudProcessGroupRepository
import app.simplecloud.simplecloud.node.repository.mongo.onlinecountstrategy.MongoOnlineCountStrategyRepository
import app.simplecloud.simplecloud.node.repository.mongo.permission.MongoPermissionGroupRepository
import com.google.inject.Inject
import org.apache.logging.log4j.LogManager

class NodeRepositoriesInitializer @Inject constructor(
    private val igniteGroupRepository: IgniteCloudProcessGroupRepository,
    private val mongoCloudProcessGroupRepository: MongoCloudProcessGroupRepository,
    private val ignitePermissionGroupRepository: IgnitePermissionGroupRepository,
    private val mongoPermissionGroupRepository: MongoPermissionGroupRepository,
    private val igniteOnlineCountStrategyRepository: IgniteOnlineCountStrategyRepository,
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
            this.igniteOnlineCountStrategyRepository.save(config.name, config).join()
        }
    }

    private fun initPermissionGroups() {
        val groups = this.mongoPermissionGroupRepository.findAll().join()
        logger.info("Found PermissionGroups: {}", groups.map { it.name })
        val configurations = groups.map { it.toConfiguration() }
        for (config in configurations) {
            this.ignitePermissionGroupRepository.save(config.name, config).join()
        }
    }

    private fun initGroups() {
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