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

package app.simplecloud.simplecloud.api.impl.request.template.group.update

import app.simplecloud.simplecloud.api.image.Image
import app.simplecloud.simplecloud.api.impl.request.template.update.AbstractLobbyTemplateUpdateRequest
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessGroupService
import app.simplecloud.simplecloud.api.request.group.update.CloudLobbyGroupUpdateRequest
import app.simplecloud.simplecloud.api.template.configuration.LobbyProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.group.CloudLobbyGroup

/**
 * Created by IntelliJ IDEA.
 * Date: 06.04.2021
 * Time: 10:03
 * @author Frederick Baier
 */
class CloudLobbyGroupUpdateRequestImpl(
    private val internalService: InternalCloudProcessGroupService,
    private val lobbyGroup: CloudLobbyGroup,
) : AbstractLobbyTemplateUpdateRequest(lobbyGroup),
    CloudLobbyGroupUpdateRequest {


    override fun getProcessTemplate(): CloudLobbyGroup {
        return this.lobbyGroup
    }

    override fun setLobbyPriority(lobbyPriority: Int): CloudLobbyGroupUpdateRequest {
        super.setLobbyPriority(lobbyPriority)
        return this
    }

    override fun setMaxMemory(memory: Int): CloudLobbyGroupUpdateRequest {
        super.setMaxMemory(memory)
        return this
    }

    override fun setMaxPlayers(players: Int): CloudLobbyGroupUpdateRequest {
        super.setMaxPlayers(players)
        return this
    }

    override fun setImage(image: Image?): CloudLobbyGroupUpdateRequest {
        super.setImage(image)
        return this
    }

    override fun setMaintenance(maintenance: Boolean): CloudLobbyGroupUpdateRequest {
        super.setMaintenance(maintenance)
        return this
    }

    override fun setJoinPermission(permission: String?): CloudLobbyGroupUpdateRequest {
        super.setJoinPermission(permission)
        return this
    }

    override fun setStateUpdating(stateUpdating: Boolean): CloudLobbyGroupUpdateRequest {
        super.setStateUpdating(stateUpdating)
        return this
    }

    override fun setStartPriority(priority: Int): CloudLobbyGroupUpdateRequest {
        super.setStartPriority(priority)
        return this
    }

    override suspend fun submit0(image: Image?) {
        val updateObj = LobbyProcessTemplateConfiguration(
            this.lobbyGroup.getName(),
            this.maxMemory,
            this.maxPlayers,
            this.maintenance,
            image?.getName(),
            this.stateUpdating,
            this.startPriority,
            this.joinPermission,
            this.lobbyPriority
        )
        return this.internalService.updateGroupInternal(updateObj)
    }
}