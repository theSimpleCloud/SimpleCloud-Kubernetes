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

import app.simplecloud.simplecloud.api.impl.messagechannel.MessageChannelManagerImpl
import app.simplecloud.simplecloud.api.impl.process.group.CloudLobbyGroupImpl
import app.simplecloud.simplecloud.api.impl.process.group.CloudProxyGroupImpl
import app.simplecloud.simplecloud.api.impl.process.group.CloudServerGroupImpl
import app.simplecloud.simplecloud.api.impl.service.DefaultTestProcessOnlineCountService
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessGroupService
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessService
import app.simplecloud.simplecloud.api.messagechannel.manager.MessageChannelManager
import app.simplecloud.simplecloud.api.process.group.CloudLobbyGroup
import app.simplecloud.simplecloud.api.process.group.CloudProxyGroup
import app.simplecloud.simplecloud.api.process.group.CloudServerGroup
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.api.service.NodeService
import app.simplecloud.simplecloud.api.service.ProcessOnlineCountService
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
) : AbstractModule() {

    override fun configure() {
        bind(Ignite::class.java).toInstance(igniteInstance)

        bind(ValidatorService::class.java).to(ValidatorServiceImpl::class.java)

        bind(NodeService::class.java).to(this.nodeServiceClass)
        bind(CloudProcessService::class.java).to(this.cloudProcessServiceClass)
        bind(InternalCloudProcessService::class.java).to(this.cloudProcessServiceClass)
        bind(CloudProcessGroupService::class.java).to(this.cloudProcessGroupServiceClass)
        bind(InternalCloudProcessGroupService::class.java).to(this.cloudProcessGroupServiceClass)
        bind(ProcessOnlineCountService::class.java).to(DefaultTestProcessOnlineCountService::class.java)

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