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

import app.simplecloud.simplecloud.api.impl.player.CloudPlayerFactory
import app.simplecloud.simplecloud.api.messagechannel.manager.MessageChannelManager
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.service.CloudPlayerService
import app.simplecloud.simplecloud.api.service.NodeService
import app.simplecloud.simplecloud.plugin.OnlineCountProvider
import app.simplecloud.simplecloud.plugin.proxy.request.ServerConnectedRequest
import app.simplecloud.simplecloud.plugin.proxy.request.ServerKickRequest
import app.simplecloud.simplecloud.plugin.proxy.request.ServerPreConnectRequest
import app.simplecloud.simplecloud.plugin.proxy.request.login.PlayerLoginRequestHandler
import app.simplecloud.simplecloud.plugin.proxy.request.login.PlayerPostLoginRequestHandler
import com.google.inject.Inject
import com.google.inject.Singleton

@Singleton
class ProxyControllerImpl @Inject constructor(
    private val messageChannelManager: MessageChannelManager,
    private val nodeService: NodeService,
    private val playerService: CloudPlayerService,
    private val playerFactory: CloudPlayerFactory,
    private val selfProcess: CloudProcess,
    private val onlineCountProvider: OnlineCountProvider
) : ProxyController {

    override fun handleLogin(request: PlayerConnectionConfiguration) {
        PlayerLoginRequestHandler(
            request,
            this.messageChannelManager,
            this.nodeService,
            this.playerFactory,
            this.playerService
        ).handle()
    }

    override fun handlePostLogin(request: PlayerConnectionConfiguration) {
        PlayerPostLoginRequestHandler(request, this.selfProcess, this.onlineCountProvider).handle()
    }

    override fun handleDisconnect(request: PlayerConnectionConfiguration) {
        TODO("Not yet implemented")
    }

    override fun handleServerPreConnect(request: ServerPreConnectRequest) {
        TODO("Not yet implemented")
    }

    override fun handleServerConnected(request: ServerConnectedRequest) {
        TODO("Not yet implemented")
    }

    override fun handleServerKick(request: ServerKickRequest) {
        TODO("Not yet implemented")
    }
}