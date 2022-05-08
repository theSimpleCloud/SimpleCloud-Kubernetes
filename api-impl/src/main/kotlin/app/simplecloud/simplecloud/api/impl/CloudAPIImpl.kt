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

import app.simplecloud.simplecloud.api.AbstractCloudAPI
import app.simplecloud.simplecloud.api.internal.InternalCloudAPI
import app.simplecloud.simplecloud.api.internal.service.InternalCloudPlayerService
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessGroupService
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessService
import app.simplecloud.simplecloud.api.internal.service.InternalPermissionGroupService
import app.simplecloud.simplecloud.api.messagechannel.manager.MessageChannelManager
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.service.NodeService

/**
 * Date: 07.05.22
 * Time: 11:20
 * @author Frederick Baier
 *
 */
open class CloudAPIImpl(
    private val processGroupService: InternalCloudProcessGroupService,
    private val processService: InternalCloudProcessService,
    private val playerService: InternalCloudPlayerService,
    private val permissionGroupService: InternalPermissionGroupService,
    private val nodeService: NodeService,
    private val messageChannelManager: MessageChannelManager,
    private val permissionFactory: Permission.Factory
) : AbstractCloudAPI(), InternalCloudAPI {

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

    override fun getNodeService(): NodeService {
        return this.nodeService
    }

    override fun getMessageChannelManager(): MessageChannelManager {
        return this.messageChannelManager
    }

    override fun getPermissionFactory(): Permission.Factory {
        return this.permissionFactory
    }
}