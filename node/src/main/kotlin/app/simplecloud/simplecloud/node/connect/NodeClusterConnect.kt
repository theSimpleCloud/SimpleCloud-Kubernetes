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

import app.simplecloud.simplecloud.api.CloudAPI
import app.simplecloud.simplecloud.api.impl.messagechannel.InternalMessageChannelProviderImpl
import app.simplecloud.simplecloud.api.impl.messagechannel.MessageChannelManagerImpl
import app.simplecloud.simplecloud.api.impl.permission.PermissionFactoryImpl
import app.simplecloud.simplecloud.api.impl.permission.group.PermissionGroupFactoryImpl
import app.simplecloud.simplecloud.api.impl.permission.player.PermissionPlayerFactoryImpl
import app.simplecloud.simplecloud.api.impl.player.factory.CloudPlayerFactoryImpl
import app.simplecloud.simplecloud.api.impl.player.factory.OfflineCloudPlayerFactoryImpl
import app.simplecloud.simplecloud.api.impl.process.factory.CloudProcessFactoryImpl
import app.simplecloud.simplecloud.api.impl.repository.distributed.*
import app.simplecloud.simplecloud.api.impl.template.group.factory.CloudLobbyGroupFactoryImpl
import app.simplecloud.simplecloud.api.impl.template.group.factory.CloudProxyGroupFactoryImpl
import app.simplecloud.simplecloud.api.impl.template.group.factory.CloudServerGroupFactoryImpl
import app.simplecloud.simplecloud.api.impl.template.group.factory.UniversalCloudProcessGroupFactory
import app.simplecloud.simplecloud.api.impl.template.statictemplate.factory.StaticLobbyTemplateFactoryImpl
import app.simplecloud.simplecloud.api.impl.template.statictemplate.factory.StaticProxyTemplateFactoryImpl
import app.simplecloud.simplecloud.api.impl.template.statictemplate.factory.StaticServerTemplateFactoryImpl
import app.simplecloud.simplecloud.api.impl.template.statictemplate.factory.UniversalStaticProcessTemplateFactory
import app.simplecloud.simplecloud.database.api.factory.DatabaseRepositories
import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.distribution.api.Distribution
import app.simplecloud.simplecloud.distribution.api.DistributionFactory
import app.simplecloud.simplecloud.eventapi.DefaultEventManager
import app.simplecloud.simplecloud.kubernetes.api.KubeAPI
import app.simplecloud.simplecloud.kubernetes.api.pod.KubePod
import app.simplecloud.simplecloud.node.api.NodeCloudAPI
import app.simplecloud.simplecloud.node.onlinestrategy.UniversalProcessOnlineCountStrategyFactory
import app.simplecloud.simplecloud.node.process.factory.ProcessShutdownHandlerFactoryImpl
import app.simplecloud.simplecloud.node.process.factory.ProcessStarterFactoryImpl
import app.simplecloud.simplecloud.node.repository.distributed.DistributedOnlineCountStrategyRepository
import app.simplecloud.simplecloud.node.service.*
import app.simplecloud.simplecloud.node.startup.prepare.RestServerStartTask
import app.simplecloud.simplecloud.restserver.api.RestServerConfig
import app.simplecloud.simplecloud.restserver.api.auth.token.TokenHandler
import org.apache.logging.log4j.LogManager

class NodeClusterConnect(
    private val distributionFactory: DistributionFactory,
    private val kubeAPI: KubeAPI,
    private val selfPod: KubePod,
    private val databaseRepositories: DatabaseRepositories,
    private val restServerConfig: RestServerConfig,
    private val tokenHandler: TokenHandler,
) {

    private val nodeBindPort = 1670

    fun connect(): NodeCloudAPI {
        app.simplecloud.simplecloud.node.connect.NodeClusterConnect.Companion.logger.info("Connecting to cluster...")
        val distribution = startDistribution()
        val distributedRepositories = initializeDistributedRepositories(distribution)
        val nodeCloudAPI = initializeServices(distribution, distributedRepositories)
        injectUserContextIntoDistribution(distribution, nodeCloudAPI, distributedRepositories)
        startRestServer(nodeCloudAPI)
        registerMessageChannels(nodeCloudAPI)
        checkForFirstNodeInCluster(distribution, distributedRepositories)
        return nodeCloudAPI
    }

    private fun injectUserContextIntoDistribution(
        distribution: Distribution,
        cloudAPI: CloudAPI,
        distributedRepositories: app.simplecloud.simplecloud.node.connect.DistributedRepositories,
    ) {
        val userContext = distribution.getUserContext()
        userContext["kubeAPI"] = this.kubeAPI
        userContext["cloudAPI"] = cloudAPI
        userContext["distributedRepositories"] = distributedRepositories
    }

    private fun initializeDistributedRepositories(distribution: Distribution): app.simplecloud.simplecloud.node.connect.DistributedRepositories {
        return app.simplecloud.simplecloud.node.connect.DistributedRepositories(
            DistributedCloudPlayerRepository(distribution),
            DistributedCloudProcessGroupRepository(distribution),
            DistributedCloudProcessRepository(distribution),
            DistributedPermissionGroupRepository(distribution),
            DistributedStaticProcessTemplateRepository(distribution),
            DistributedOnlineCountStrategyRepository(distribution)
        )
    }

    private fun registerMessageChannels(nodeCloudAPI: NodeCloudAPI) {
        app.simplecloud.simplecloud.node.connect.MessageChannelsInitializer(
            nodeCloudAPI,
            InternalMessageChannelProviderImpl(nodeCloudAPI.getMessageChannelManager())
        ).initializeMessageChannels()
    }
    private fun checkForFirstNodeInCluster(
        distribution: Distribution,
        distributedRepositories: app.simplecloud.simplecloud.node.connect.DistributedRepositories,
    ) {
        if (distribution.getServers().size == 1) {
            app.simplecloud.simplecloud.node.connect.ClusterInitializer(
                distribution,
                distributedRepositories,
                this.databaseRepositories
            ).initialize()
        }
    }

    private fun startRestServer(nodeCloudAPI: NodeCloudAPI) {
        val authService = app.simplecloud.simplecloud.node.connect.RestAuthServiceImpl(
            nodeCloudAPI.getCloudPlayerService(),
            this.tokenHandler
        )
        return RestServerStartTask(
            nodeCloudAPI,
            this.restServerConfig.controllerHandlerFactory,
            this.restServerConfig.restServer,
            authService
        ).run()
    }

    private fun initializeServices(
        distribution: Distribution,
        distributedRepositories: app.simplecloud.simplecloud.node.connect.DistributedRepositories,
    ): NodeCloudAPI {
        val eventManager = DefaultEventManager()
        val nodeService = NodeServiceImpl(distribution)
        val universalGroupFactory = UniversalCloudProcessGroupFactory(
            CloudLobbyGroupFactoryImpl(),
            CloudProxyGroupFactoryImpl(),
            CloudServerGroupFactoryImpl()
        )
        val universalStaticProcessTemplateFactory = UniversalStaticProcessTemplateFactory(
            StaticLobbyTemplateFactoryImpl(),
            StaticProxyTemplateFactoryImpl(),
            StaticServerTemplateFactoryImpl()
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

        val staticProcessTemplateService = StaticProcessTemplateServiceImpl(
            universalStaticProcessTemplateFactory,
            distributedRepositories.staticProcessTemplateRepository,
            databaseRepositories.staticProcessTemplateRepository
        )

        val processFactory = CloudProcessFactoryImpl()
        val cloudProcessService = CloudProcessServiceImpl(
            processFactory,
            distributedRepositories.cloudProcessRepository,
            eventManager,
            ProcessStarterFactoryImpl(processFactory, this.kubeAPI),
            ProcessShutdownHandlerFactoryImpl(
                this.kubeAPI.getPodService()
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
            staticProcessTemplateService,
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
        app.simplecloud.simplecloud.node.connect.NodeClusterConnect.Companion.logger.info("Connecting to {}", addresses)
        val actualPort = this.kubeAPI.getNetworkService().requestPort(this.selfPod, this.nodeBindPort)
        return this.distributionFactory.createServer(actualPort, addresses)
    }

    private fun getOtherNodesAddressesToConnectTo(): List<Address> {
        //TODO get other addresses
        return emptyList()
    }

    companion object {
        private val logger =
            LogManager.getLogger(app.simplecloud.simplecloud.node.connect.NodeClusterConnect::class.java)
    }

}