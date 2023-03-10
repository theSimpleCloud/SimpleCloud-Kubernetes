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
import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedCloudProcessGroupRepository
import app.simplecloud.simplecloud.api.impl.service.AbstractCloudProcessGroupService
import app.simplecloud.simplecloud.api.impl.template.group.factory.UniversalCloudProcessGroupFactory
import app.simplecloud.simplecloud.api.internal.messagechannel.InternalMessageChannelProvider
import app.simplecloud.simplecloud.api.node.Node
import app.simplecloud.simplecloud.api.service.NodeService
import app.simplecloud.simplecloud.api.template.configuration.AbstractProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.group.CloudProcessGroup

class CloudProcessGroupServiceImpl(
    distributedRepository: DistributedCloudProcessGroupRepository,
    processGroupFactory: UniversalCloudProcessGroupFactory,
    internalMessageChannelProvider: InternalMessageChannelProvider,
    private val nodeService: NodeService
) : AbstractCloudProcessGroupService(
    distributedRepository, processGroupFactory
) {

    private val deleteMessageChannel = internalMessageChannelProvider.getInternalDeleteGroupChannel()

    private val updateMessageChannel = internalMessageChannelProvider.getInternalUpdateGroupChannel()

    private val createMessageChannel = internalMessageChannelProvider.getInternalCreateGroupChannel()

    override suspend fun createGroupInternal0(configuration: AbstractProcessTemplateConfiguration) {
        val node = this.nodeService.findFirst().await()
        sendCreateRequestToNode(configuration, node)
    }

    override suspend fun updateGroupInternal0(configuration: AbstractProcessTemplateConfiguration) {
        val node = this.nodeService.findFirst().await()
        sendUpdateRequestToNode(configuration, node)
    }

    override suspend fun deleteGroupInternal(group: CloudProcessGroup) {
        val node = this.nodeService.findFirst().await()
        sendDeleteRequestToNode(group.getName(), node)
    }

    private suspend fun sendUpdateRequestToNode(configuration: AbstractProcessTemplateConfiguration, node: Node) {
        this.updateMessageChannel.createMessageRequest(configuration, node).submit().await()
    }

    private suspend fun sendDeleteRequestToNode(name: String, node: Node) {
        this.deleteMessageChannel.createMessageRequest(name, node).submit().await()
    }

    private suspend fun sendCreateRequestToNode(configuration: AbstractProcessTemplateConfiguration, node: Node) {
        this.createMessageChannel.createMessageRequest(configuration, node).submit().await()
    }

}