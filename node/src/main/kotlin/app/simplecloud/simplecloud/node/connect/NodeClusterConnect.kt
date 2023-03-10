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
import app.simplecloud.simplecloud.api.impl.cache.CacheHandlerImpl
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
import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.distribution.api.Distribution
import app.simplecloud.simplecloud.distribution.api.DistributionFactory
import app.simplecloud.simplecloud.eventapi.DefaultEventManager
import app.simplecloud.simplecloud.kubernetes.api.KubeAPI
import app.simplecloud.simplecloud.kubernetes.api.pod.KubePod
import app.simplecloud.simplecloud.module.api.impl.NodeCloudAPIImpl
import app.simplecloud.simplecloud.module.api.impl.error.ErrorFactoryImpl
import app.simplecloud.simplecloud.module.api.impl.ftp.FtpServerFactoryImpl
import app.simplecloud.simplecloud.module.api.impl.ftp.start.FtpServerStarterImpl
import app.simplecloud.simplecloud.module.api.impl.ftp.stop.FtpServerStopperImpl
import app.simplecloud.simplecloud.module.api.impl.repository.distributed.DistributedErrorRepository
import app.simplecloud.simplecloud.module.api.impl.repository.distributed.DistributedFtpServerRepository
import app.simplecloud.simplecloud.module.api.impl.service.DefaultErrorService
import app.simplecloud.simplecloud.module.api.impl.service.DefaultFtpServerService
import app.simplecloud.simplecloud.module.api.internal.service.InternalNodeCloudAPI
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.ResourceRequestHandler
import app.simplecloud.simplecloud.node.onlinestrategy.UniversalProcessOnlineCountStrategyFactory
import app.simplecloud.simplecloud.node.process.factory.ProcessShutdownHandlerFactoryImpl
import app.simplecloud.simplecloud.node.process.factory.ProcessStarterFactoryImpl
import app.simplecloud.simplecloud.node.repository.distributed.DistributedOnlineCountStrategyRepository
import app.simplecloud.simplecloud.node.resource.ResourceDefinitionRegisterer
import app.simplecloud.simplecloud.node.resourcedefinition.handler.ResourceRequestHandlerImpl
import app.simplecloud.simplecloud.node.resourcedefinition.web.ResourceDefinitionRouteRegisterer
import app.simplecloud.simplecloud.node.service.*
import app.simplecloud.simplecloud.node.startup.prepare.ControllerRegisterer
import app.simplecloud.simplecloud.node.startup.prepare.PreparedNode
import app.simplecloud.simplecloud.restserver.api.RestServerConfig
import org.apache.logging.log4j.LogManager

class NodeClusterConnect(
    private val distributionFactory: DistributionFactory,
    private val kubeAPI: KubeAPI,
    private val selfPod: KubePod,
    private val preparedNode: PreparedNode,
    private val restServerConfig: RestServerConfig,
) {

    private val databaseRepositories = this.preparedNode.repositories
    private val tokenHandler = this.preparedNode.tokenHandler
    private val nodeModuleLoader = this.preparedNode.nodeModuleLoader
    private val localAPI = this.preparedNode.localAPI
    private val nodeBindPort = 1670

    fun connect(): NodeCloudAPIImpl {
        logger.info("Connecting to cluster...")
        val distribution = startDistribution()
        val distributedRepositories = initializeDistributedRepositories(distribution)
        val nodeCloudAPI = initializeServices(distribution, distributedRepositories)
        injectUserContextIntoDistribution(distribution, nodeCloudAPI, distributedRepositories)
        registerWebControllers(nodeCloudAPI)
        registerMessageChannels(nodeCloudAPI)
        checkForFirstNodeInCluster(distribution, distributedRepositories, nodeCloudAPI.getResourceRequestHandler())
        handleClusterActive(nodeCloudAPI)
        return nodeCloudAPI
    }

    private fun handleClusterActive(nodeCloudAPI: InternalNodeCloudAPI) {
        this.nodeModuleLoader.startModuleSchedulerWatcher(nodeCloudAPI.getDistribution())
        this.nodeModuleLoader.onClusterActive(nodeCloudAPI)
    }

    private fun injectUserContextIntoDistribution(
        distribution: Distribution,
        cloudAPI: CloudAPI,
        distributedRepositories: DistributedRepositories,
    ) {
        val userContext = distribution.getUserContext()
        userContext["kubeAPI"] = this.kubeAPI
        userContext["cloudAPI"] = cloudAPI
        userContext["distributedRepositories"] = distributedRepositories
    }

    private fun initializeDistributedRepositories(distribution: Distribution): DistributedRepositories {
        return DistributedRepositories(
            DistributedCloudPlayerRepository(distribution),
            DistributedCloudProcessGroupRepository(distribution),
            DistributedCloudProcessRepository(distribution),
            DistributedPermissionGroupRepository(distribution),
            DistributedStaticProcessTemplateRepository(distribution),
            DistributedOnlineCountStrategyRepository(distribution),
            DistributedErrorRepository(distribution),
            DistributedFtpServerRepository(distribution),
        )
    }

    private fun registerMessageChannels(nodeCloudAPI: NodeCloudAPIImpl) {
        MessageChannelsInitializer(
            nodeCloudAPI,
            nodeCloudAPI.getInternalMessageChannelProvider(),
            this.preparedNode.environmentVariables
        ).initializeMessageChannels()
    }

    private fun checkForFirstNodeInCluster(
        distribution: Distribution,
        distributedRepositories: DistributedRepositories,
        resourceRequestHandler: ResourceRequestHandler,
    ) {
        if (distribution.getServers().size == 1) {
            ClusterInitializer(
                distribution,
                distributedRepositories,
                resourceRequestHandler
            ).initialize()
        }
    }

    private fun registerWebControllers(nodeCloudAPI: NodeCloudAPIImpl) {
        val restServer = this.restServerConfig.restServer
        val authService = RestAuthServiceImpl(
            nodeCloudAPI.getCloudPlayerService(),
            this.tokenHandler
        )
        restServer.setAuthService(authService)
        ControllerRegisterer(
            nodeCloudAPI,
            authService,
            this.preparedNode.environmentVariables
        ).registerControllers()

        ResourceDefinitionRouteRegisterer(
            this.restServerConfig.restServer,
            nodeCloudAPI.getResourceDefinitionService(),
            nodeCloudAPI.getResourceRequestHandler()
        ).registerRoutes()
    }

    private fun initializeServices(
        distribution: Distribution,
        distributedRepositories: DistributedRepositories,
    ): NodeCloudAPIImpl {
        val resourceDefinitionService = ResourceDefinitionServiceImpl()
        val requestHandler = ResourceRequestHandlerImpl(
            this.databaseRepositories.resourceRepository,
            resourceDefinitionService
        )

        val controllerHandler = this.restServerConfig.controllerHandlerFactory.create(this.restServerConfig.restServer)
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
            UniversalProcessOnlineCountStrategyFactory(),
            requestHandler
        )
        val cloudProcessGroupService = CloudProcessGroupServiceImpl(
            universalGroupFactory,
            distributedRepositories.cloudProcessGroupRepository,
            requestHandler
        )

        val staticProcessTemplateService = StaticProcessTemplateServiceImpl(
            universalStaticProcessTemplateFactory,
            distributedRepositories.staticProcessTemplateRepository,
            requestHandler
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
            distributedRepositories.permissionGroupRepository,
            permissionGroupFactory,
            permissionFactory,
            requestHandler
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

        val errorService = DefaultErrorService(distributedRepositories.errorRepository, ErrorFactoryImpl())

        val ftpServerFactory = FtpServerFactoryImpl()
        val ftpService = DefaultFtpServerService(
            distributedRepositories.ftpServerRepository,
            ftpServerFactory,
            FtpServerStarterImpl(this.kubeAPI, ftpServerFactory),
            FtpServerStopperImpl(this.kubeAPI)
        )

        val messageChannelManager = MessageChannelManagerImpl(nodeService, cloudProcessService, distribution)
        val selfComponent = nodeService.findByDistributionComponent(distribution.getSelfComponent()).join()
        val cacheHandler = CacheHandlerImpl(distribution)

        ResourceDefinitionRegisterer(resourceDefinitionService, distributedRepositories).registerDefinitions()

        return NodeCloudAPIImpl(
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
            distribution,
            cacheHandler,
            errorService,
            nodeProcessOnlineStrategyService,
            this.localAPI,
            this.kubeAPI,
            ftpService,
            InternalMessageChannelProviderImpl(messageChannelManager),
            controllerHandler,
            resourceDefinitionService,
            requestHandler
        )
    }

    private fun startDistribution(): Distribution {
        val addresses = getOtherNodesAddressesToConnectTo()
        logger.info("Connecting to {}", addresses)
        val actualPort = this.kubeAPI.getNetworkService().requestPort(this.selfPod, this.nodeBindPort)
        return this.distributionFactory.createServer(
            actualPort,
            addresses,
            this.nodeModuleLoader.getModuleClassLoader()
        )
    }

    private fun getOtherNodesAddressesToConnectTo(): List<Address> {
        //TODO get other addresses
        return emptyList()
    }

    companion object {
        private val logger =
            LogManager.getLogger(NodeClusterConnect::class.java)
    }

}