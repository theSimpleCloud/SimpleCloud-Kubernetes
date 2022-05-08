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

import app.simplecloud.simplecloud.api.impl.CloudAPIImpl
import app.simplecloud.simplecloud.api.internal.service.InternalCloudPlayerService
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessGroupService
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessService
import app.simplecloud.simplecloud.api.internal.service.InternalPermissionGroupService
import app.simplecloud.simplecloud.api.messagechannel.manager.MessageChannelManager
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.service.NodeService
import app.simplecloud.simplecloud.eventapi.EventManager

/**
 * Date: 07.05.22
 * Time: 21:16
 * @author Frederick Baier
 *
 */
class PluginCloudAPI(
    localNetworkComponentName: String,
    val internalProcessGroupService: InternalCloudProcessGroupService,
    val internalProcessService: InternalCloudProcessService,
    val internalPlayerService: InternalCloudPlayerService,
    val internalPermissionGroupService: InternalPermissionGroupService,
    nodeService: NodeService,
    messageChannelManager: MessageChannelManager,
    eventManager: EventManager,
    permissionFactory: Permission.Factory,
) : CloudAPIImpl(
    localNetworkComponentName,
    internalProcessGroupService,
    internalProcessService,
    internalPlayerService,
    internalPermissionGroupService,
    nodeService,
    messageChannelManager,
    eventManager,
    permissionFactory
)