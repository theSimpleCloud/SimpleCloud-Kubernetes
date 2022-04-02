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

package app.simplecloud.simplecloud.api.impl.guice

import app.simplecloud.simplecloud.api.impl.ignite.IgniteQueryHandler
import app.simplecloud.simplecloud.api.impl.ignite.IgniteQueryHandlerImpl
import app.simplecloud.simplecloud.api.impl.messagechannel.InternalMessageChannelProviderImpl
import app.simplecloud.simplecloud.api.impl.messagechannel.MessageChannelManagerImpl
import app.simplecloud.simplecloud.api.impl.permission.PermissionImpl
import app.simplecloud.simplecloud.api.impl.permission.group.PermissionGroupImpl
import app.simplecloud.simplecloud.api.impl.permission.player.PermissionPlayerImpl
import app.simplecloud.simplecloud.api.impl.player.CloudPlayerFactory
import app.simplecloud.simplecloud.api.impl.player.CloudPlayerImpl
import app.simplecloud.simplecloud.api.impl.player.OfflineCloudPlayerFactory
import app.simplecloud.simplecloud.api.impl.player.OfflineCloudPlayerImpl
import app.simplecloud.simplecloud.api.impl.process.CloudProcessImpl
import app.simplecloud.simplecloud.api.impl.process.factory.CloudProcessFactory
import app.simplecloud.simplecloud.api.impl.process.group.CloudLobbyGroupImpl
import app.simplecloud.simplecloud.api.impl.process.group.CloudProxyGroupImpl
import app.simplecloud.simplecloud.api.impl.process.group.CloudServerGroupImpl
import app.simplecloud.simplecloud.api.internal.messagechannel.InternalMessageChannelProvider
import app.simplecloud.simplecloud.api.internal.service.InternalCloudPlayerService
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessGroupService
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessService
import app.simplecloud.simplecloud.api.internal.service.InternalPermissionGroupService
import app.simplecloud.simplecloud.api.messagechannel.manager.MessageChannelManager
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.PermissionPlayer
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.player.OfflineCloudPlayer
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.process.group.CloudLobbyGroup
import app.simplecloud.simplecloud.api.process.group.CloudProxyGroup
import app.simplecloud.simplecloud.api.process.group.CloudServerGroup
import app.simplecloud.simplecloud.api.service.*
import com.google.inject.AbstractModule
import com.google.inject.assistedinject.FactoryModuleBuilder
import org.apache.ignite.Ignite

/**
 * Created by IntelliJ IDEA.
 * Date: 31.05.2021
 * Time: 11:31
 * @author Frederick Baier
 */
class CloudAPIBinderModule(
    private val igniteInstance: Ignite,
    private val nodeServiceClass: Class<out NodeService>,
    private val cloudProcessServiceClass: Class<out InternalCloudProcessService>,
    private val cloudProcessGroupServiceClass: Class<out InternalCloudProcessGroupService>,
    private val cloudPlayerServiceClass: Class<out InternalCloudPlayerService>,
    private val permissionGroupService: Class<out InternalPermissionGroupService>,
) : AbstractModule() {

    override fun configure() {
        bind(Ignite::class.java).toInstance(igniteInstance)

        install(
            FactoryModuleBuilder()
                .implement(Permission::class.java, PermissionImpl::class.java)
                .build(Permission.Factory::class.java)
        )

        install(
            FactoryModuleBuilder()
                .implement(PermissionGroup::class.java, PermissionGroupImpl::class.java)
                .build(PermissionGroup.Factory::class.java)
        )

        install(
            FactoryModuleBuilder()
                .implement(PermissionPlayer::class.java, PermissionPlayerImpl::class.java)
                .build(PermissionPlayer.Factory::class.java)
        )

        install(
            FactoryModuleBuilder()
                .implement(CloudProcess::class.java, CloudProcessImpl::class.java)
                .build(CloudProcessFactory::class.java)
        )

        install(
            FactoryModuleBuilder()
                .implement(CloudPlayer::class.java, CloudPlayerImpl::class.java)
                .build(CloudPlayerFactory::class.java)
        )

        install(
            FactoryModuleBuilder()
                .implement(OfflineCloudPlayer::class.java, OfflineCloudPlayerImpl::class.java)
                .build(OfflineCloudPlayerFactory::class.java)
        )

        bind(PermissionGroupService::class.java).to(this.permissionGroupService)
        bind(InternalPermissionGroupService::class.java).to(this.permissionGroupService)
        bind(NodeService::class.java).to(this.nodeServiceClass)
        bind(CloudPlayerService::class.java).to(this.cloudPlayerServiceClass)
        bind(InternalCloudPlayerService::class.java).to(this.cloudPlayerServiceClass)
        bind(CloudProcessService::class.java).to(this.cloudProcessServiceClass)
        bind(InternalCloudProcessService::class.java).to(this.cloudProcessServiceClass)
        bind(CloudProcessGroupService::class.java).to(this.cloudProcessGroupServiceClass)
        bind(InternalCloudProcessGroupService::class.java).to(this.cloudProcessGroupServiceClass)

        bind(IgniteQueryHandler::class.java).to(IgniteQueryHandlerImpl::class.java)
        bind(MessageChannelManager::class.java).to(MessageChannelManagerImpl::class.java)
        bind(InternalMessageChannelProvider::class.java).to(InternalMessageChannelProviderImpl::class.java)

        install(
            FactoryModuleBuilder()
                .implement(CloudLobbyGroup::class.java, CloudLobbyGroupImpl::class.java)
                .build(CloudLobbyGroup.Factory::class.java)
        )

        install(
            FactoryModuleBuilder()
                .implement(CloudProxyGroup::class.java, CloudProxyGroupImpl::class.java)
                .build(CloudProxyGroup.Factory::class.java)
        )

        install(
            FactoryModuleBuilder()
                .implement(CloudServerGroup::class.java, CloudServerGroupImpl::class.java)
                .build(CloudServerGroup.Factory::class.java)
        )

    }

}