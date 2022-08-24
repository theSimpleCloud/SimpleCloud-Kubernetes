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

package app.simplecloud.simplecloud.node.defaultcontroller.v1.handler

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.impl.image.ImageImpl
import app.simplecloud.simplecloud.api.request.template.ProcessLobbyTemplateUpdateRequest
import app.simplecloud.simplecloud.api.request.template.ProcessProxyTemplateUpdateRequest
import app.simplecloud.simplecloud.api.template.ProcessTemplate
import app.simplecloud.simplecloud.api.template.configuration.AbstractProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.configuration.LobbyProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.configuration.ProxyProcessTemplateConfiguration

/**
 * Created by IntelliJ IDEA.
 * Date: 07/07/2021
 * Time: 20:46
 * @author Frederick Baier
 */
class ProcessTemplateUpdateHandler(
    private val processTemplate: ProcessTemplate,
    private val targetConfig: AbstractProcessTemplateConfiguration,
) {

    suspend fun update() {
        val request = this.processTemplate.createUpdateRequest()
        request.setMaxMemory(this.targetConfig.maxMemory)
        request.setMaxPlayers(this.targetConfig.maxPlayers)
        request.setImage(ImageImpl.fromName(this.targetConfig.imageName))
        request.setMaintenance(this.targetConfig.maintenance)
        request.setJoinPermission(this.targetConfig.joinPermission)
        request.setStateUpdating(this.targetConfig.stateUpdating)
        request.setStartPriority(this.targetConfig.startPriority)

        if (request is ProcessProxyTemplateUpdateRequest) {
            this.targetConfig as ProxyProcessTemplateConfiguration
            request.setStartPort(this.targetConfig.startPort)
        }

        if (request is ProcessLobbyTemplateUpdateRequest) {
            this.targetConfig as LobbyProcessTemplateConfiguration
            request.setLobbyPriority(this.targetConfig.lobbyPriority)
        }

        return request.submit().await()
    }

}