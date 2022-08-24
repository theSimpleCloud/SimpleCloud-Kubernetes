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

package app.simplecloud.simplecloud.api.impl

import app.simplecloud.simplecloud.api.internal.InternalCloudAPI
import app.simplecloud.simplecloud.api.internal.service.*
import app.simplecloud.simplecloud.api.messagechannel.manager.MessageChannelManager
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.service.NodeService
import app.simplecloud.simplecloud.distribution.api.Distribution
import app.simplecloud.simplecloud.eventapi.EventManager

/**
 * Date: 07.05.22
 * Time: 11:20
 * @author Frederick Baier
 *
 */
open class CloudAPIImpl(
    private val localNetworkComponentName: String,
    private val processGroupService: InternalCloudProcessGroupService,
    private val staticTemplateService: InternalStaticProcessTemplateService,
    private val processService: InternalCloudProcessService,
    private val playerService: InternalCloudPlayerService,
    private val permissionGroupService: InternalPermissionGroupService,
    private val nodeService: NodeService,
    private val messageChannelManager: MessageChannelManager,
    private val eventManager: EventManager,
    private val permissionFactory: Permission.Factory,
    private val distribution: Distribution,
) : InternalCloudAPI {

    override fun getLocalNetworkComponentName(): String {
        return this.localNetworkComponentName
    }

    override fun getProcessGroupService(): InternalCloudProcessGroupService {
        return this.processGroupService
    }

    override fun getProcessService(): InternalCloudProcessService {
        return this.processService
    }

    override fun getCloudPlayerService(): InternalCloudPlayerService {
        return this.playerService
    }

    override fun getPermissionGroupService(): InternalPermissionGroupService {
        return this.permissionGroupService
    }

    override fun getStaticProcessTemplateService(): InternalStaticProcessTemplateService {
        return this.staticTemplateService
    }

    override fun getNodeService(): NodeService {
        return this.nodeService
    }

    override fun getMessageChannelManager(): MessageChannelManager {
        return this.messageChannelManager
    }

    override fun getEventManager(): EventManager {
        return this.eventManager
    }

    override fun getPermissionFactory(): Permission.Factory {
        return this.permissionFactory
    }

    override fun getDistribution(): Distribution {
        return this.distribution
    }

}