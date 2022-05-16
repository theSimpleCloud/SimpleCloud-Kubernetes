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

import app.simplecloud.simplecloud.api.impl.messagechannel.InternalMessageChannelProviderImpl
import app.simplecloud.simplecloud.api.impl.messagechannel.MessageChannelManagerImpl
import app.simplecloud.simplecloud.api.impl.permission.PermissionFactoryImpl
import app.simplecloud.simplecloud.api.impl.permission.group.PermissionGroupFactoryImpl
import app.simplecloud.simplecloud.api.impl.permission.player.PermissionPlayerFactoryImpl
import app.simplecloud.simplecloud.api.impl.player.factory.CloudPlayerFactoryImpl
import app.simplecloud.simplecloud.api.impl.player.factory.OfflineCloudPlayerFactoryImpl
import app.simplecloud.simplecloud.api.impl.process.factory.CloudProcessFactoryImpl
import app.simplecloud.simplecloud.api.impl.process.group.factory.CloudLobbyGroupFactoryImpl
import app.simplecloud.simplecloud.api.impl.process.group.factory.CloudProxyGroupFactoryImpl
import app.simplecloud.simplecloud.api.impl.process.group.factory.CloudServerGroupFactoryImpl
import app.simplecloud.simplecloud.api.impl.process.group.factory.UniversalCloudProcessGroupFactory
import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedCloudPlayerRepository
import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedCloudProcessGroupRepository
import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedCloudProcessRepository
import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedPermissionGroupRepository
import app.simplecloud.simplecloud.database.api.factory.DatabaseRepositories
import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.distribution.api.Distribution
import app.simplecloud.simplecloud.distribution.api.DistributionFactory
import app.simplecloud.simplecloud.eventapi.DefaultEventManager
import app.simplecloud.simplecloud.kubernetes.api.KubeAPI
import app.simplecloud.simplecloud.node.api.NodeCloudAPI
import app.simplecloud.simplecloud.node.onlinestrategy.UniversalProcessOnlineCountStrategyFactory
import app.simplecloud.simplecloud.node.process.factory.ProcessShutdownHandlerFactoryImpl
import app.simplecloud.simplecloud.node.process.factory.ProcessStarterFactoryImpl
import app.simplecloud.simplecloud.node.repository.distributed.DistributedOnlineCountStrategyRepository
import app.simplecloud.simplecloud.node.service.*
import app.simplecloud.simplecloud.node.startup.prepare.RestServerStartTask
import app.simplecloud.simplecloud.node.task.NodeOnlineProcessesChecker
import app.simplecloud.simplecloud.restserver.api.RestServerConfig
import app.simplecloud.simplecloud.restserver.api.auth.token.TokenHandler
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager

class NodeClusterConnect(
    private val distributionFactory: DistributionFactory,
    private val kubeAPI: KubeAPI,
    private val databaseRepositories: DatabaseRepositories,
    private val restServerConfig: RestServerConfig,
    private val tokenHandler: TokenHandler
) {

    private val nodeBindPort = 1670

    fun connect(): NodeCloudAPI {
        logger.info("Connecting to cluster...")
        val distribution = startDistribution()
        val distributedRepositories = initializeDistributedRepositories(distribution)
        val nodeCloudAPI = initializeServices(distribution, distributedRepositories)
        startRestServer(nodeCloudAPI)
        registerMessageChannels(nodeCloudAPI)
        checkForFirstNodeInCluster(distribution, distributedRepositories)
        checkOnlineProcesses(nodeCloudAPI)
        return nodeCloudAPI
    }

    private fun initializeDistributedRepositories(distribution: Distribution): DistributedRepositories {
        return DistributedRepositories(
            DistributedCloudPlayerRepository(distribution),
            DistributedCloudProcessGroupRepository(distribution),
            DistributedCloudProcessRepository(distribution),
            DistributedPermissionGroupRepository(distribution),
            DistributedOnlineCountStrategyRepository(distribution)
        )
    }

    private fun registerMessageChannels(nodeCloudAPI: NodeCloudAPI) {
        MessageChannelsInitializer(
            nodeCloudAPI,
            InternalMessageChannelProviderImpl(nodeCloudAPI.getMessageChannelManager())
        ).initializeMessageChannels()
    }

    private fun checkOnlineProcesses(nodeCloudAPI: NodeCloudAPI) {
        logger.info("Checking for online tasks")
        val nodeOnlineProcessesChecker = NodeOnlineProcessesChecker(
            nodeCloudAPI.getProcessGroupService(),
            nodeCloudAPI.getProcessService(),
            nodeCloudAPI.getOnlineStrategyService()
        )
        runBlocking {
            nodeOnlineProcessesChecker.checkOnlineCount()
        }
    }

    private fun checkForFirstNodeInCluster(
        distribution: Distribution,
        distributedRepositories: DistributedRepositories
    ) {
        if (distribution.getServers().size == 1) {
            val nodeRepositoriesInitializer = NodeRepositoriesInitializer(
                distributedRepositories.cloudProcessGroupRepository,
                this.databaseRepositories.cloudProcessGroupRepository,
                distributedRepositories.permissionGroupRepository,
                this.databaseRepositories.permissionGroupRepository,
                distributedRepositories.distributedOnlineCountStrategyRepository,
                this.databaseRepositories.onlineCountStrategyRepository
            )
            nodeRepositoriesInitializer.initializeRepositories()
        }
    }

    private fun startRestServer(nodeCloudAPI: NodeCloudAPI) {
        val authService = RestAuthServiceImpl(nodeCloudAPI.getCloudPlayerService(), this.tokenHandler)
        return RestServerStartTask(
            nodeCloudAPI,
            this.restServerConfig.controllerHandlerFactory,
            this.restServerConfig.restServer,
            authService
        ).run()
    }

    private fun initializeServices(
        distribution: Distribution,
        distributedRepositories: DistributedRepositories
    ): NodeCloudAPI {
        val eventManager = DefaultEventManager()
        val nodeService = NodeServiceImpl(distribution)
        val universalGroupFactory = UniversalCloudProcessGroupFactory(
            CloudLobbyGroupFactoryImpl(),
            CloudProxyGroupFactoryImpl(),
            CloudServerGroupFactoryImpl()
        )
        val nodeProcessOnlineStrategyService = NodeProcessOnlineStrategyServiceImpl(
            distributedRepositories.distributedOnlineCountStrategyRepository,
            databaseRepositories.onlineCountStrategyRepository,
            UniversalProcessOnlineCountStrategyFactory()
        )
        val cloudProcessGroupService = CloudProcessGroupServiceImpl(
            universalGroupFactory,
            distributedRepositories.cloudProcessGroupRepository,
            databaseRepositories.cloudProcessGroupRepository
        )

        val processFactory = CloudProcessFactoryImpl()
        val cloudProcessService = CloudProcessServiceImpl(
            processFactory,
            distributedRepositories.cloudProcessRepository,
            eventManager,
            ProcessStarterFactoryImpl(processFactory, this.kubeAPI),
            ProcessShutdownHandlerFactoryImpl(
                this.kubeAPI.getPodService(),
                distributedRepositories.cloudProcessRepository
            ),
            this.kubeAPI.getPodService()
        )

        val permissionFactory = PermissionFactoryImpl()
        val permissionGroupFactory = PermissionGroupFactoryImpl(permissionFactory)
        val permissionGroupService = PermissionGroupServiceImpl(
            this.databaseRepositories.permissionGroupRepository,
            distributedRepositories.permissionGroupRepository,
            permissionGroupFactory,
            permissionFactory
        )

        val permissionPlayerFactory = PermissionPlayerFactoryImpl(permissionGroupService, permissionFactory)
        val cloudPlayerService = CloudPlayerServiceImpl(
            distributedRepositories.cloudPlayerRepository,
            CloudPlayerFactoryImpl(cloudProcessService, permissionFactory, permissionPlayerFactory),
            this.databaseRepositories.offlineCloudPlayerRepository,
            OfflineCloudPlayerFactoryImpl(permissionFactory, permissionPlayerFactory),
            cloudProcessService,
            cloudProcessGroupService
        )
        val messageChannelManager = MessageChannelManagerImpl(nodeService, cloudProcessService, distribution)
        val selfComponent = nodeService.findByDistributionComponent(distribution.getSelfComponent()).join()
        return NodeCloudAPI(
            selfComponent.getName(),
            cloudProcessGroupService,
            cloudProcessService,
            cloudPlayerService,
            permissionGroupService,
            nodeService,
            messageChannelManager,
            eventManager,
            permissionFactory,
            nodeProcessOnlineStrategyService,
        )
    }

    private fun startDistribution(): Distribution {
        val addresses = getOtherNodesAddressesToConnectTo()
        logger.info("Connecting to {}", addresses)
        return this.distributionFactory.createServer(this.nodeBindPort, addresses)
    }

    private fun getOtherNodesAddressesToConnectTo(): List<Address> {
        //TODO get other addresses
        return emptyList()
    }

    companion object {
        private val logger = LogManager.getLogger(NodeClusterConnect::class.java)
    }

}