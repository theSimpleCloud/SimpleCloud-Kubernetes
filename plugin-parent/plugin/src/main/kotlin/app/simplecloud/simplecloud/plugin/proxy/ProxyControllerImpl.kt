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

package app.simplecloud.simplecloud.plugin.proxy

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.internal.configutation.PlayerLoginConfiguration
import app.simplecloud.simplecloud.api.internal.service.InternalCloudPlayerService
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.plugin.OnlineCountUpdater
import app.simplecloud.simplecloud.plugin.proxy.request.*
import app.simplecloud.simplecloud.plugin.proxy.request.handler.PlayerDisconnectRequestHandler
import app.simplecloud.simplecloud.plugin.proxy.request.handler.PlayerPostLoginRequestHandler
import app.simplecloud.simplecloud.plugin.proxy.request.handler.PlayerServerConnectedRequestHandler
import app.simplecloud.simplecloud.plugin.proxy.request.handler.PlayerServerPreConnectRequestHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ProxyControllerImpl(
    private val playerService: InternalCloudPlayerService,
    private val processService: CloudProcessService,
    private val processGroupService: CloudProcessGroupService,
    private val onlineCountUpdater: OnlineCountUpdater
) : ProxyController {

    override suspend fun handleLogin(request: PlayerLoginConfiguration): CloudPlayer {
        return this.playerService.loginPlayer(request)
    }

    override fun handlePostLogin(request: PlayerConnectionConfiguration) {
        PlayerPostLoginRequestHandler(request, this.onlineCountUpdater).handle()
    }

    override fun handleDisconnect(request: PlayerDisconnectRequest) {
        PlayerDisconnectRequestHandler(
            request,
            this.onlineCountUpdater,
            this.playerService
        ).handler()
    }

    override fun handleServerPreConnect(request: ServerPreConnectRequest): ServerPreConnectResponse {
        return runBlocking {
            return@runBlocking PlayerServerPreConnectRequestHandler(
                request,
                processService,
                playerService,
                processGroupService
            ).handle()
        }
    }

    override fun handleServerConnected(request: ServerConnectedRequest) {
        CloudScope.launch {
            PlayerServerConnectedRequestHandler(
                request,
                playerService
            ).handle()
        }
    }

    override fun handleServerKick(request: ServerKickRequest) {
        TODO("Not yet implemented")
    }
}