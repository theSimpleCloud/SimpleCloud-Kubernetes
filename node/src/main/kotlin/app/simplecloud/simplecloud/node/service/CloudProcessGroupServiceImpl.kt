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

package app.simplecloud.simplecloud.node.service

import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedCloudProcessGroupRepository
import app.simplecloud.simplecloud.api.impl.service.AbstractCloudProcessGroupService
import app.simplecloud.simplecloud.api.impl.template.group.factory.UniversalCloudProcessGroupFactory
import app.simplecloud.simplecloud.api.template.configuration.AbstractProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.configuration.LobbyProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.configuration.ProxyProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.group.CloudProcessGroup
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.ResourceRequestHandler
import app.simplecloud.simplecloud.node.resource.group.V1Beta1LobbyGroupSpec
import app.simplecloud.simplecloud.node.resource.group.V1Beta1ProxyGroupSpec
import app.simplecloud.simplecloud.node.resource.group.V1Beta1ServerGroupSpec

class CloudProcessGroupServiceImpl(
    processGroupFactory: UniversalCloudProcessGroupFactory,
    distributedRepository: DistributedCloudProcessGroupRepository,
    private val resourceRequestHandler: ResourceRequestHandler,
) : AbstractCloudProcessGroupService(
    distributedRepository, processGroupFactory
) {

    override suspend fun createGroupInternal0(configuration: AbstractProcessTemplateConfiguration) {
        val kind = getKindFromTemplateConfiguration(configuration)
        val spec = convertConfigurationToSpec(configuration)
        this.resourceRequestHandler.handleCreate("core", kind, "v1beta1", configuration.name, spec)
    }

    override suspend fun updateGroupInternal0(configuration: AbstractProcessTemplateConfiguration) {
        val kind = getKindFromTemplateConfiguration(configuration)
        val spec = convertConfigurationToSpec(configuration)
        this.resourceRequestHandler.handleUpdate("core", kind, "v1beta1", configuration.name, spec)
    }

    override suspend fun deleteGroupInternal(group: CloudProcessGroup) {
        val kind = getKindFromTemplateConfiguration(group.toConfiguration())
        this.resourceRequestHandler.handleDelete("core", kind, "v1beta1", group.getName())
    }

    private fun getKindFromTemplateConfiguration(configuration: AbstractProcessTemplateConfiguration): String {
        return when (configuration) {
            is LobbyProcessTemplateConfiguration -> "LobbyGroup"
            is ProxyProcessTemplateConfiguration -> "ProxyGroup"
            else -> "ServerGroup"
        }
    }

    private fun convertConfigurationToSpec(configuration: AbstractProcessTemplateConfiguration): Any {
        return when (configuration) {
            is LobbyProcessTemplateConfiguration -> V1Beta1LobbyGroupSpec(
                configuration.maxMemory,
                configuration.maxPlayers,
                configuration.maintenance,
                configuration.imageName,
                configuration.stateUpdating,
                configuration.startPriority,
                configuration.joinPermission,
                configuration.active,
                configuration.lobbyPriority
            )

            is ProxyProcessTemplateConfiguration -> V1Beta1ProxyGroupSpec(
                configuration.maxMemory,
                configuration.maxPlayers,
                configuration.maintenance,
                configuration.imageName,
                configuration.stateUpdating,
                configuration.startPriority,
                configuration.joinPermission,
                configuration.active
            )

            else -> V1Beta1ServerGroupSpec(
                configuration.maxMemory,
                configuration.maxPlayers,
                configuration.maintenance,
                configuration.imageName,
                configuration.stateUpdating,
                configuration.startPriority,
                configuration.joinPermission,
                configuration.active
            )
        }
    }


}