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
import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedStaticProcessTemplateRepository
import app.simplecloud.simplecloud.api.permission.configuration.PermissionConfiguration
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.process.onlinestrategy.configuration.ProcessOnlineCountStrategyConfiguration
import app.simplecloud.simplecloud.api.template.configuration.LobbyProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.configuration.ProxyProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.configuration.ServerProcessTemplateConfiguration
import app.simplecloud.simplecloud.database.api.DatabaseLinkRepository
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.ResourceRequestHandler
import app.simplecloud.simplecloud.node.repository.distributed.DistributedLinkRepository
import app.simplecloud.simplecloud.node.repository.distributed.DistributedOnlineCountStrategyRepository
import app.simplecloud.simplecloud.node.resource.group.V1Beta1LobbyGroupSpec
import app.simplecloud.simplecloud.node.resource.group.V1Beta1ProxyGroupSpec
import app.simplecloud.simplecloud.node.resource.group.V1Beta1ServerGroupSpec
import app.simplecloud.simplecloud.node.resource.onlinestrategy.V1Beta1ProcessOnlineCountStrategySpec
import app.simplecloud.simplecloud.node.resource.permissiongroup.V1Beta1PermissionGroupSpec
import app.simplecloud.simplecloud.node.resource.staticserver.V1Beta1StaticLobbySpec
import app.simplecloud.simplecloud.node.resource.staticserver.V1Beta1StaticProxySpec
import app.simplecloud.simplecloud.node.resource.staticserver.V1Beta1StaticServerSpec
import org.apache.logging.log4j.LogManager

class NodeRepositoriesInitializer(
    private val requestHandler: ResourceRequestHandler,
    private val databaseLinkRepository: DatabaseLinkRepository,
    private val distributedGroupRepository: DistributedCloudProcessGroupRepository,
    private val distributedPermissionGroupRepository: DistributedPermissionGroupRepository,
    private val distributedOnlineCountStrategyRepository: DistributedOnlineCountStrategyRepository,
    private val distributedStaticProcessTemplateRepository: DistributedStaticProcessTemplateRepository,
    private val distributedLinkRepository: DistributedLinkRepository,
) {

    fun initializeRepositories() {
        logger.info("Initializing Distributed Repositories")
        initLinks()
        initGroups()
        initPermissionGroups()
        initOnlineCountStrategies()
        initStaticTemplates()
    }

    private fun initLinks() {
        val linkConfigurations = databaseLinkRepository.loadAll()
        logger.info(
            "Found Links: {}",
            linkConfigurations.map { it.oneResourceName + " " + it.linkType + " " + it.manyResourceName })
        for (linkConfiguration in linkConfigurations) {
            this.distributedLinkRepository.save(
                linkConfiguration.linkType + "/" + linkConfiguration.oneResourceName,
                linkConfiguration
            )
        }
    }

    private fun initOnlineCountStrategies() {
        val specResults = this.requestHandler.handleGetAllSpec<V1Beta1ProcessOnlineCountStrategySpec>(
            "core",
            "ProcessOnlineCountStrategy",
            "v1beta1"
        )
        val configurations = specResults.map { convertProcessOnlineCountSpecToConfig(it.getName(), it.getSpec()) }

        logger.info("Found OnlineCountStrategies: {}", configurations.map { it.name })
        for (config in configurations) {
            this.distributedOnlineCountStrategyRepository.save(config.name, config).join()
        }
    }

    private fun initPermissionGroups() {
        val specResults = this.requestHandler.handleGetAllSpec<V1Beta1PermissionGroupSpec>(
            "core",
            "PermissionGroup",
            "v1beta1"
        )
        val configurations = specResults.map { convertPermissionGroupSpecToConfig(it.getName(), it.getSpec()) }

        logger.info("Found PermissionGroups: {}", configurations.map { it.name })
        for (config in configurations) {
            this.distributedPermissionGroupRepository.save(config.name, config).join()
        }
    }

    private fun initStaticTemplates() {
        initStaticLobbies()
        initStaticProxies()
        initStaticServers()
    }

    private fun initGroups() {
        initLobbyGroups()
        initProxyGroups()
        initServerGroups()
    }

    private fun initStaticLobbies() {
        val specResults = this.requestHandler.handleGetAllSpec<V1Beta1StaticLobbySpec>(
            "core",
            "StaticLobby",
            "v1beta1"
        )
        val configurations = specResults.map { convertStaticLobbySpecToConfig(it.getName(), it.getSpec()) }

        logger.info("Found Static Lobbies: {}", configurations.map { it.name })
        for (config in configurations) {
            this.distributedStaticProcessTemplateRepository.save(config.name, config).join()
        }
    }

    private fun initStaticProxies() {
        val specResults = this.requestHandler.handleGetAllSpec<V1Beta1StaticProxySpec>(
            "core",
            "StaticProxy",
            "v1beta1"
        )
        val configurations = specResults.map { convertStaticProxySpecToConfig(it.getName(), it.getSpec()) }

        logger.info("Found Static Proxies: {}", configurations.map { it.name })
        for (config in configurations) {
            this.distributedStaticProcessTemplateRepository.save(config.name, config).join()
        }
    }

    private fun initStaticServers() {
        val specResults = this.requestHandler.handleGetAllSpec<V1Beta1StaticServerSpec>(
            "core",
            "StaticServer",
            "v1beta1"
        )
        val configurations = specResults.map { convertStaticServerSpecToConfig(it.getName(), it.getSpec()) }

        logger.info("Found Static Servers: {}", configurations.map { it.name })
        for (config in configurations) {
            this.distributedStaticProcessTemplateRepository.save(config.name, config).join()
        }
    }

    private fun convertProcessOnlineCountSpecToConfig(
        name: String,
        spec: V1Beta1ProcessOnlineCountStrategySpec,
    ): ProcessOnlineCountStrategyConfiguration {
        return ProcessOnlineCountStrategyConfiguration(
            name,
            spec.className,
            createMap(spec.dataKeys, spec.dataValues)
        )
    }

    private fun createMap(keys: Array<String>, values: Array<String>): Map<String, String> {
        val map = mutableMapOf<String, String>()
        for (index in keys.indices) {
            map[keys[0]] = values[0]
        }
        return map
    }

    private fun convertPermissionGroupSpecToConfig(
        name: String,
        spec: V1Beta1PermissionGroupSpec,
    ): PermissionGroupConfiguration {
        return PermissionGroupConfiguration(
            name,
            spec.priority,
            spec.permissions.map {
                PermissionConfiguration(
                    it.permissionString,
                    it.active,
                    it.expiresAtTimestamp,
                    it.targetProcessGroup
                )
            }
        )
    }

    private fun convertStaticServerSpecToConfig(
        name: String,
        spec: V1Beta1StaticServerSpec,
    ): ServerProcessTemplateConfiguration {
        return ServerProcessTemplateConfiguration(
            name,
            spec.maxMemory,
            spec.maxPlayers,
            spec.maintenance,
            spec.imageName,
            spec.stateUpdating,
            spec.startPriority,
            spec.joinPermission,
            spec.active
        )
    }

    private fun convertStaticLobbySpecToConfig(
        name: String,
        spec: V1Beta1StaticLobbySpec,
    ): LobbyProcessTemplateConfiguration {
        return LobbyProcessTemplateConfiguration(
            name,
            spec.maxMemory,
            spec.maxPlayers,
            spec.maintenance,
            spec.imageName,
            spec.stateUpdating,
            spec.startPriority,
            spec.joinPermission,
            spec.active,
            spec.lobbyPriority
        )
    }

    private fun convertStaticProxySpecToConfig(
        name: String,
        spec: V1Beta1StaticProxySpec,
    ): ProxyProcessTemplateConfiguration {
        return ProxyProcessTemplateConfiguration(
            name,
            spec.maxMemory,
            spec.maxPlayers,
            spec.maintenance,
            spec.imageName,
            spec.stateUpdating,
            spec.startPriority,
            spec.joinPermission,
            spec.active,
            spec.startPort
        )
    }

    private fun initServerGroups() {
        val specResults = this.requestHandler.handleGetAllSpec<V1Beta1ServerGroupSpec>(
            "core",
            "ServerGroup",
            "v1beta1"
        )
        val configurations = specResults.map { convertServerGroupSpecToConfig(it.getName(), it.getSpec()) }

        logger.info("Found Server Groups: {}", configurations.map { it.name })
        for (config in configurations) {
            this.distributedGroupRepository.save(config.name, config).join()
        }
    }

    private fun initProxyGroups() {
        val specResults = this.requestHandler.handleGetAllSpec<V1Beta1ProxyGroupSpec>(
            "core",
            "ProxyGroup",
            "v1beta1"
        )
        val configurations = specResults.map { convertProxyGroupSpecToConfig(it.getName(), it.getSpec()) }

        logger.info("Found Proxy Groups: {}", configurations.map { it.name })
        for (config in configurations) {
            this.distributedGroupRepository.save(config.name, config).join()
        }
    }

    private fun initLobbyGroups() {
        val specResults = this.requestHandler.handleGetAllSpec<V1Beta1LobbyGroupSpec>(
            "core",
            "LobbyGroup",
            "v1beta1"
        )
        val configurations = specResults.map { convertLobbyGroupSpecToConfig(it.getName(), it.getSpec()) }

        logger.info("Found Lobby Groups: {}", configurations.map { it.name })
        for (config in configurations) {
            this.distributedGroupRepository.save(config.name, config).join()
        }
    }

    private fun convertServerGroupSpecToConfig(
        name: String,
        spec: V1Beta1ServerGroupSpec,
    ): ServerProcessTemplateConfiguration {
        return ServerProcessTemplateConfiguration(
            name,
            spec.maxMemory,
            spec.maxPlayers,
            spec.maintenance,
            spec.imageName,
            spec.stateUpdating,
            spec.startPriority,
            spec.joinPermission,
            spec.active
        )
    }

    private fun convertLobbyGroupSpecToConfig(
        name: String,
        spec: V1Beta1LobbyGroupSpec,
    ): LobbyProcessTemplateConfiguration {
        return LobbyProcessTemplateConfiguration(
            name,
            spec.maxMemory,
            spec.maxPlayers,
            spec.maintenance,
            spec.imageName,
            spec.stateUpdating,
            spec.startPriority,
            spec.joinPermission,
            spec.active,
            spec.lobbyPriority
        )
    }

    private fun convertProxyGroupSpecToConfig(
        name: String,
        spec: V1Beta1ProxyGroupSpec,
    ): ProxyProcessTemplateConfiguration {
        return ProxyProcessTemplateConfiguration(
            name,
            spec.maxMemory,
            spec.maxPlayers,
            spec.maintenance,
            spec.imageName,
            spec.stateUpdating,
            spec.startPriority,
            spec.joinPermission,
            spec.active,
            spec.startPort
        )
    }

    companion object {
        private val logger = LogManager.getLogger(NodeRepositoriesInitializer::class.java)
    }

}