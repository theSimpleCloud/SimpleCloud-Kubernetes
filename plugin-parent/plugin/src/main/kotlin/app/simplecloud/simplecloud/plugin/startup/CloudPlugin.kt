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

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.impl.cache.CacheHandlerImpl
import app.simplecloud.simplecloud.api.impl.env.EnvironmentVariables
import app.simplecloud.simplecloud.api.impl.messagechannel.InternalMessageChannelProviderImpl
import app.simplecloud.simplecloud.api.impl.messagechannel.MessageChannelManagerImpl
import app.simplecloud.simplecloud.api.impl.permission.PermissionFactoryImpl
import app.simplecloud.simplecloud.api.impl.permission.group.PermissionGroupFactoryImpl
import app.simplecloud.simplecloud.api.impl.permission.player.PermissionPlayerFactoryImpl
import app.simplecloud.simplecloud.api.impl.player.factory.CloudPlayerFactoryImpl
import app.simplecloud.simplecloud.api.impl.player.factory.OfflineCloudPlayerFactoryImpl
import app.simplecloud.simplecloud.api.impl.process.factory.CloudProcessFactoryImpl
import app.simplecloud.simplecloud.api.impl.repository.distributed.*
import app.simplecloud.simplecloud.api.impl.service.DefaultCloudStateService
import app.simplecloud.simplecloud.api.impl.template.group.factory.CloudLobbyGroupFactoryImpl
import app.simplecloud.simplecloud.api.impl.template.group.factory.CloudProxyGroupFactoryImpl
import app.simplecloud.simplecloud.api.impl.template.group.factory.CloudServerGroupFactoryImpl
import app.simplecloud.simplecloud.api.impl.template.group.factory.UniversalCloudProcessGroupFactory
import app.simplecloud.simplecloud.api.impl.template.statictemplate.factory.StaticLobbyTemplateFactoryImpl
import app.simplecloud.simplecloud.api.impl.template.statictemplate.factory.StaticProxyTemplateFactoryImpl
import app.simplecloud.simplecloud.api.impl.template.statictemplate.factory.StaticServerTemplateFactoryImpl
import app.simplecloud.simplecloud.api.impl.template.statictemplate.factory.UniversalStaticProcessTemplateFactory
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.distribution.api.Distribution
import app.simplecloud.simplecloud.distribution.api.DistributionFactory
import app.simplecloud.simplecloud.eventapi.DefaultEventManager
import app.simplecloud.simplecloud.plugin.startup.service.*
import kotlinx.coroutines.runBlocking
import java.util.*

class CloudPlugin(
    private val distributionFactory: DistributionFactory,
    private val environmentVariables: EnvironmentVariables,
    private val nodeAddress: Address,
) {

    val selfProcessId = UUID.fromString(this.environmentVariables.get("SIMPLECLOUD_PROCESS_ID"))
    private val distribution = startDistribution()
    private val distributedRepositories = initializeDistributedRepositories(distribution)
    val pluginCloudAPI = initializeServices(distribution, distributedRepositories)


    init {
        SelfDistributedProcessUpdater(
            distribution,
            SelfProcessProvider(selfProcessId, pluginCloudAPI.getProcessService())
        ).updateProcessBlocking()
    }

    private fun initializeServices(
        distribution: Distribution,
        distributedRepositories: DistributedRepositories,
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
        val universalStaticProcessTemplateFactory = UniversalStaticProcessTemplateFactory(
            StaticLobbyTemplateFactoryImpl(),
            StaticProxyTemplateFactoryImpl(),
            StaticServerTemplateFactoryImpl()
        )

        val cloudProcessGroupService = CloudProcessGroupServiceImpl(
            distributedRepositories.cloudProcessGroupRepository,
            universalGroupFactory,
            internalMessageChannelProvider,
            nodeService
        )

        val staticProcessTemplateService = StaticProcessTemplateServiceImpl(
            distributedRepositories.staticProcessTemplateRepository,
            universalStaticProcessTemplateFactory,
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
            CloudPlayerFactoryImpl(cloudProcessService, permissionFactory, permissionPlayerFactory),
            OfflineCloudPlayerFactoryImpl(permissionFactory, permissionPlayerFactory)
        )

        val selfComponent = findSelfProcess(cloudProcessService)
        val cacheHandler = CacheHandlerImpl(this.distribution)
        val cloudStateService = DefaultCloudStateService(cacheHandler)
        return PluginCloudAPI(
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
            cloudStateService
        )
    }

    private fun findSelfProcess(processService: CloudProcessService): CloudProcess = runBlocking {
        try {
            return@runBlocking processService.findByUniqueId(this@CloudPlugin.selfProcessId).await()
        } catch (ex: NoSuchElementException) {
            throw CloudPluginStartException("Unable to find self process by id: ${this@CloudPlugin.selfProcessId}", ex)
        }
    }

    private fun initializeDistributedRepositories(distribution: Distribution): DistributedRepositories {
        return DistributedRepositories(
            DistributedCloudPlayerRepository(distribution),
            DistributedCloudProcessGroupRepository(distribution),
            DistributedCloudProcessRepository(distribution),
            DistributedPermissionGroupRepository(distribution),
            DistributedStaticProcessTemplateRepository(distribution)
        )
    }

    private fun startDistribution(): Distribution {
        return this.distributionFactory.createClient(this.nodeAddress)
    }

    class CloudPluginStartException(message: String, cause: Throwable) : Exception(message, cause)

}