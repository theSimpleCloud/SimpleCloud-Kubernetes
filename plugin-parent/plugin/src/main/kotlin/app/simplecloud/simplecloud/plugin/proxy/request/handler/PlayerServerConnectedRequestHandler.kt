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

package app.simplecloud.simplecloud.plugin.proxy.request.handler

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.internal.request.player.InternalCloudPlayerUpdateRequest
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.service.CloudPlayerService
import app.simplecloud.simplecloud.plugin.proxy.request.ServerConnectedRequest

class PlayerServerConnectedRequestHandler(
    private val request: ServerConnectedRequest,
    private val playerService: CloudPlayerService
) {
    suspend fun handle() {
        val playerUniqueId = request.playerConnection.uniqueId
        val cloudPlayer = this.playerService.findOnlinePlayerByUniqueId(playerUniqueId).await()
        updateConnectedServer(cloudPlayer)
    }

    private suspend fun updateConnectedServer(cloudPlayer: CloudPlayer) {
        val updateRequest = cloudPlayer.createUpdateRequest()
        updateRequest as InternalCloudPlayerUpdateRequest
        updateRequest.setConnectedServerName(this.request.serverName)
        updateRequest.submit().await()
    }



}
