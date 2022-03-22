/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package app.simplecloud.simplecloud.api.impl.guice

import app.simplecloud.simplecloud.api.impl.ignite.IgniteQueryHandler
import app.simplecloud.simplecloud.api.impl.ignite.IgniteQueryHandlerImpl
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
import app.simplecloud.simplecloud.api.internal.service.InternalCloudPlayerService
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessGroupService
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessService
import app.simplecloud.simplecloud.api.internal.service.InternalPermissionGroupService
import app.simplecloud.simplecloud.api.messagechannel.manager.MessageChannelManager
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.PermissionPlayer
import app.simplecloud.simplecloud.api.permission.service.PermissionGroupService
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.player.OfflineCloudPlayer
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.process.group.CloudLobbyGroup
import app.simplecloud.simplecloud.api.process.group.CloudProxyGroup
import app.simplecloud.simplecloud.api.process.group.CloudServerGroup
import app.simplecloud.simplecloud.api.service.*
import app.simplecloud.simplecloud.api.validator.ValidatorService
import app.simplecloud.simplecloud.api.validator.ValidatorServiceImpl
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
    private val processOnlineCountService: Class<out ProcessOnlineCountService>,
    private val permissionGroupService: Class<out InternalPermissionGroupService>,
) : AbstractModule() {

    override fun configure() {
        bind(Ignite::class.java).toInstance(igniteInstance)

        bind(ValidatorService::class.java).to(ValidatorServiceImpl::class.java)

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
        bind(ProcessOnlineCountService::class.java).to(this.processOnlineCountService)
        bind(NodeService::class.java).to(this.nodeServiceClass)
        bind(CloudPlayerService::class.java).to(this.cloudPlayerServiceClass)
        bind(InternalCloudPlayerService::class.java).to(this.cloudPlayerServiceClass)
        bind(CloudProcessService::class.java).to(this.cloudProcessServiceClass)
        bind(InternalCloudProcessService::class.java).to(this.cloudProcessServiceClass)
        bind(CloudProcessGroupService::class.java).to(this.cloudProcessGroupServiceClass)
        bind(InternalCloudProcessGroupService::class.java).to(this.cloudProcessGroupServiceClass)

        bind(IgniteQueryHandler::class.java).to(IgniteQueryHandlerImpl::class.java)
        bind(MessageChannelManager::class.java).to(MessageChannelManagerImpl::class.java)

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