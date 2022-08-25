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

package app.simplecloud.simplecloud.api.impl.request.template.statictemplates.update

import app.simplecloud.simplecloud.api.image.Image
import app.simplecloud.simplecloud.api.impl.request.template.update.AbstractLobbyTemplateUpdateRequest
import app.simplecloud.simplecloud.api.internal.service.InternalStaticProcessTemplateService
import app.simplecloud.simplecloud.api.request.statictemplate.update.StaticLobbyTemplateUpdateRequest
import app.simplecloud.simplecloud.api.template.configuration.LobbyProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.static.StaticLobbyTemplate

/**
 * Date: 17.08.22
 * Time: 14:58
 * @author Frederick Baier
 *
 */
class StaticLobbyTemplateUpdateRequestImpl(
    private val staticTemplate: StaticLobbyTemplate,
    private val internalService: InternalStaticProcessTemplateService,
) : AbstractLobbyTemplateUpdateRequest(staticTemplate), StaticLobbyTemplateUpdateRequest {

    override fun getProcessTemplate(): StaticLobbyTemplate {
        return this.staticTemplate
    }

    override fun setLobbyPriority(lobbyPriority: Int): StaticLobbyTemplateUpdateRequest {
        super.setLobbyPriority(lobbyPriority)
        return this
    }

    override fun setMaxMemory(memory: Int): StaticLobbyTemplateUpdateRequest {
        super.setMaxMemory(memory)
        return this
    }

    override fun setMaxPlayers(players: Int): StaticLobbyTemplateUpdateRequest {
        super.setMaxPlayers(players)
        return this
    }

    override fun setImage(image: Image?): StaticLobbyTemplateUpdateRequest {
        super.setImage(image)
        return this
    }

    override fun setMaintenance(maintenance: Boolean): StaticLobbyTemplateUpdateRequest {
        super.setMaintenance(maintenance)
        return this
    }

    override fun setJoinPermission(permission: String?): StaticLobbyTemplateUpdateRequest {
        super.setJoinPermission(permission)
        return this
    }

    override fun setStateUpdating(stateUpdating: Boolean): StaticLobbyTemplateUpdateRequest {
        super.setStateUpdating(stateUpdating)
        return this
    }

    override fun setStartPriority(priority: Int): StaticLobbyTemplateUpdateRequest {
        super.setStartPriority(priority)
        return this
    }

    override fun setActive(active: Boolean): StaticLobbyTemplateUpdateRequest {
        super.setActive(active)
        return this
    }

    override suspend fun submit0(image: Image?) {
        val updateObj = LobbyProcessTemplateConfiguration(
            this.staticTemplate.getName(),
            this.maxMemory,
            this.maxPlayers,
            this.maintenance,
            image?.getName(),
            this.stateUpdating,
            this.startPriority,
            this.joinPermission,
            this.active,
            this.lobbyPriority
        )
        this.internalService.updateStaticTemplateInternal(updateObj)
    }

}