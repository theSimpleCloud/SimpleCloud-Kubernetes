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

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.internal.service.InternalCloudPlayerService
import app.simplecloud.simplecloud.plugin.OnlineCountUpdater
import app.simplecloud.simplecloud.plugin.proxy.request.PlayerDisconnectRequest
import kotlinx.coroutines.launch

class PlayerDisconnectRequestHandler(
    private val request: PlayerDisconnectRequest,
    private val onlineCountUpdater: OnlineCountUpdater,
    private val playerService: InternalCloudPlayerService
) {
    fun handler() {
        playerService.logoutPlayer(request.connection.uniqueId)
        CloudScope.launch {
            onlineCountUpdater.updateSelfOnlineCount()
        }
    }

}
