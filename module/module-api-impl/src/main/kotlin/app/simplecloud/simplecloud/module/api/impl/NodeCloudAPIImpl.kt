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

package app.simplecloud.simplecloud.module.api.impl

import app.simplecloud.simplecloud.api.cache.CacheHandler
import app.simplecloud.simplecloud.api.impl.CloudAPIImpl
import app.simplecloud.simplecloud.api.internal.messagechannel.InternalMessageChannelProvider
import app.simplecloud.simplecloud.api.internal.service.*
import app.simplecloud.simplecloud.api.messagechannel.manager.MessageChannelManager
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.service.NodeService
import app.simplecloud.simplecloud.distribution.api.Distribution
import app.simplecloud.simplecloud.eventapi.EventManager
import app.simplecloud.simplecloud.kubernetes.api.KubeAPI
import app.simplecloud.simplecloud.module.api.LocalAPI
import app.simplecloud.simplecloud.module.api.internal.service.InternalErrorService
import app.simplecloud.simplecloud.module.api.internal.service.InternalFtpServerService
import app.simplecloud.simplecloud.module.api.internal.service.InternalNodeCloudAPI
import app.simplecloud.simplecloud.module.api.internal.service.InternalNodeProcessOnlineCountStrategyService
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceDefinitionService
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.ResourceRequestHandler
import app.simplecloud.simplecloud.restserver.api.controller.ControllerHandler

/**
 * Date: 07.05.22
 * Time: 11:24
 * @author Frederick Baier
 *
 */
class NodeCloudAPIImpl(
    localNetworkComponentName: String,
    processGroupService: InternalCloudProcessGroupService,
    templateService: InternalStaticProcessTemplateService,
    processService: InternalCloudProcessService,
    playerService: InternalCloudPlayerService,
    permissionGroupService: InternalPermissionGroupService,
    nodeService: NodeService,
    messageChannelManager: MessageChannelManager,
    eventManager: EventManager,
    permissionFactory: Permission.Factory,
    distribution: Distribution,
    cacheHandler: CacheHandler,
    cloudStateService: InternalCloudStateService,
    private val errorService: InternalErrorService,
    private val onlineStrategyService: InternalNodeProcessOnlineCountStrategyService,
    private val localAPI: LocalAPI,
    private val kubeAPI: KubeAPI,
    private val ftpService: InternalFtpServerService,
    private val messageChannelProvider: InternalMessageChannelProvider,
    private val controllerHandler: ControllerHandler,
    private val resourceDefinitionService: ResourceDefinitionService,
    private val resourceRequestHandler: ResourceRequestHandler,
) : CloudAPIImpl(
    localNetworkComponentName,
    processGroupService,
    templateService,
    processService,
    playerService,
    permissionGroupService,
    nodeService,
    messageChannelManager,
    eventManager,
    permissionFactory,
    distribution,
    cacheHandler,
    cloudStateService
), InternalNodeCloudAPI {

    override fun getOnlineStrategyService(): InternalNodeProcessOnlineCountStrategyService {
        return this.onlineStrategyService
    }

    override fun getErrorService(): InternalErrorService {
        return this.errorService
    }

    override fun getLocalAPI(): LocalAPI {
        return this.localAPI
    }

    override fun getKubeAPI(): KubeAPI {
        return this.kubeAPI
    }

    override fun getFtpService(): InternalFtpServerService {
        return this.ftpService
    }

    override fun getInternalMessageChannelProvider(): InternalMessageChannelProvider {
        return this.messageChannelProvider
    }

    override fun getWebControllerHandler(): ControllerHandler {
        return this.controllerHandler
    }

    override fun getResourceDefinitionService(): ResourceDefinitionService {
        return this.resourceDefinitionService
    }

    override fun getResourceRequestHandler(): ResourceRequestHandler {
        return this.resourceRequestHandler
    }

}