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

package app.simplecloud.simplecloud.plugin.startup.service

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedPermissionGroupRepository
import app.simplecloud.simplecloud.api.impl.service.AbstractPermissionGroupService
import app.simplecloud.simplecloud.api.internal.messagechannel.InternalMessageChannelProvider
import app.simplecloud.simplecloud.api.node.Node
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.service.NodeService

/**
 * Date: 20.03.22
 * Time: 13:39
 * @author Frederick Baier
 *
 */
class PermissionGroupServiceImpl(
    private val distributedRepository: DistributedPermissionGroupRepository,
    private val groupFactory: PermissionGroup.Factory,
    private val permissionFactory: Permission.Factory,
    internalMessageChannelProvider: InternalMessageChannelProvider,
    private val nodeService: NodeService,
) : AbstractPermissionGroupService(distributedRepository, groupFactory, permissionFactory) {


    private val deleteMessageChannel = internalMessageChannelProvider.getInternalDeletePermissionGroupChannel()

    private val updateMessageChannel = internalMessageChannelProvider.getInternalUpdatePermissionGroupChannel()

    private val createMessageChannel = internalMessageChannelProvider.getInternalCreatePermissionGroupChannel()

    override suspend fun createGroupInternal0(configuration: PermissionGroupConfiguration) {
        val node = this.nodeService.findFirst().await()
        sendCreateRequestToNode(configuration, node)
    }

    override suspend fun updateGroupInternal(configuration: PermissionGroupConfiguration) {
        val node = this.nodeService.findFirst().await()
        sendUpdateRequestToNode(configuration, node)
    }

    override suspend fun deleteGroupInternal(group: PermissionGroup) {
        val node = this.nodeService.findFirst().await()
        sendDeleteRequestToNode(group, node)
    }

    private suspend fun sendCreateRequestToNode(configuration: PermissionGroupConfiguration, node: Node) {
        this.createMessageChannel.createMessageRequest(configuration, node).submit().await()
    }

    private suspend fun sendUpdateRequestToNode(configuration: PermissionGroupConfiguration, node: Node) {
        this.updateMessageChannel.createMessageRequest(configuration, node).submit().await()
    }

    private suspend fun sendDeleteRequestToNode(group: PermissionGroup, node: Node) {
        this.deleteMessageChannel.createMessageRequest(group.getName(), node).submit().await()
    }

}