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

package app.simplecloud.simplecloud.plugin.startup

import app.simplecloud.simplecloud.api.impl.messagechannel.InternalMessageChannelProviderImpl
import app.simplecloud.simplecloud.api.impl.messagechannel.MessageChannelManagerImpl
import app.simplecloud.simplecloud.api.impl.permission.PermissionFactoryImpl
import app.simplecloud.simplecloud.api.impl.permission.group.PermissionGroupFactoryImpl
import app.simplecloud.simplecloud.api.impl.permission.player.PermissionPlayerFactoryImpl
import app.simplecloud.simplecloud.api.impl.player.factory.CloudPlayerFactoryImpl
import app.simplecloud.simplecloud.api.impl.process.factory.CloudProcessFactoryImpl
import app.simplecloud.simplecloud.api.impl.process.group.factory.CloudLobbyGroupFactoryImpl
import app.simplecloud.simplecloud.api.impl.process.group.factory.CloudProxyGroupFactoryImpl
import app.simplecloud.simplecloud.api.impl.process.group.factory.CloudServerGroupFactoryImpl
import app.simplecloud.simplecloud.api.impl.process.group.factory.UniversalCloudProcessGroupFactory
import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedCloudPlayerRepository
import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedCloudProcessGroupRepository
import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedCloudProcessRepository
import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedPermissionGroupRepository
import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.distribution.api.Distribution
import app.simplecloud.simplecloud.distribution.api.DistributionFactory
import app.simplecloud.simplecloud.eventapi.DefaultEventManager
import app.simplecloud.simplecloud.plugin.startup.service.*

class CloudPlugin(
    private val distributionFactory: DistributionFactory
) {

    private val distribution = startDistribution()
    private val distributedRepositories = initializeDistributedRepositories(distribution)
    val pluginCloudAPI = initializeServices(distribution, distributedRepositories)

    init {
        SelfDistributedProcessUpdater(distribution, SelfProcessProvider(pluginCloudAPI.getProcessService()))
            .updateProcessBlocking()
    }

    private fun initializeServices(
        distribution: Distribution,
        distributedRepositories: DistributedRepositories
    ): PluginCloudAPI {
        val eventManager = DefaultEventManager()
        val nodeService = NodeServiceImpl(distribution)
        val processFactory = CloudProcessFactoryImpl()
        val cloudProcessService = CloudProcessServiceImpl(
            processFactory,
            distributedRepositories.cloudProcessRepository,
            eventManager,
            nodeService
        )
        val messageChannelManager = MessageChannelManagerImpl(nodeService, cloudProcessService, distribution)
        val internalMessageChannelProvider = InternalMessageChannelProviderImpl(messageChannelManager)
        cloudProcessService.initializeMessageChannels(internalMessageChannelProvider)

        val universalGroupFactory = UniversalCloudProcessGroupFactory(
            CloudLobbyGroupFactoryImpl(),
            CloudProxyGroupFactoryImpl(),
            CloudServerGroupFactoryImpl()
        )
        val cloudProcessGroupService = CloudProcessGroupServiceImpl(
            distributedRepositories.cloudProcessGroupRepository,
            universalGroupFactory,
            internalMessageChannelProvider,
            nodeService
        )


        val permissionFactory = PermissionFactoryImpl()
        val permissionGroupFactory = PermissionGroupFactoryImpl(permissionFactory)
        val permissionGroupService = PermissionGroupServiceImpl(
            distributedRepositories.permissionGroupRepository,
            permissionGroupFactory,
            permissionFactory,
            internalMessageChannelProvider,
            nodeService
        )

        val permissionPlayerFactory = PermissionPlayerFactoryImpl(permissionGroupService, permissionFactory)
        val cloudPlayerService = CloudPlayerServiceImpl(
            distributedRepositories.cloudPlayerRepository,
            nodeService,
            internalMessageChannelProvider,
            CloudPlayerFactoryImpl(cloudProcessService, permissionFactory, permissionPlayerFactory)
        )
        val selfComponent = cloudProcessService.findByDistributionComponent(distribution.getSelfComponent()).join()
        return PluginCloudAPI(
            selfComponent.getName(),
            cloudProcessGroupService,
            cloudProcessService,
            cloudPlayerService,
            permissionGroupService,
            nodeService,
            messageChannelManager,
            eventManager,
            permissionFactory,
        )
    }

    private fun initializeDistributedRepositories(distribution: Distribution): DistributedRepositories {
        return DistributedRepositories(
            DistributedCloudPlayerRepository(distribution),
            DistributedCloudProcessGroupRepository(distribution),
            DistributedCloudProcessRepository(distribution),
            DistributedPermissionGroupRepository(distribution)
        )
    }

    private fun startDistribution(): Distribution {
        val nodeAddress = Address.fromIpString("distribution:1670")
        return this.distributionFactory.createClient(nodeAddress)
    }

}