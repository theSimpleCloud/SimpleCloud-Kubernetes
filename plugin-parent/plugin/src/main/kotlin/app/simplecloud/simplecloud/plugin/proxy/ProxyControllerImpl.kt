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
import app.simplecloud.simplecloud.api.impl.player.CloudPlayerFactory
import app.simplecloud.simplecloud.api.impl.repository.ignite.IgniteCloudPlayerRepository
import app.simplecloud.simplecloud.api.messagechannel.manager.MessageChannelManager
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.api.service.CloudPlayerService
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.api.service.NodeService
import app.simplecloud.simplecloud.plugin.proxy.request.ServerConnectedRequest
import app.simplecloud.simplecloud.plugin.proxy.request.ServerKickRequest
import app.simplecloud.simplecloud.plugin.proxy.request.ServerPreConnectRequest
import app.simplecloud.simplecloud.plugin.proxy.request.ServerPreConnectResponse
import app.simplecloud.simplecloud.plugin.proxy.request.handler.*
import app.simplecloud.simplecloud.plugin.startup.SelfProcessProvider
import app.simplecloud.simplecloud.plugin.util.OnlineCountProvider
import com.google.inject.Inject
import com.google.inject.Singleton
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Singleton
class ProxyControllerImpl @Inject constructor(
    private val messageChannelManager: MessageChannelManager,
    private val nodeService: NodeService,
    private val playerService: CloudPlayerService,
    private val processService: CloudProcessService,
    private val processGroupService: CloudProcessGroupService,
    private val playerFactory: CloudPlayerFactory,
    private val selfProcessProvider: SelfProcessProvider,
    private val onlineCountProvider: OnlineCountProvider,
    private val igniteCloudPlayerRepository: IgniteCloudPlayerRepository,
) : ProxyController {

    override fun handleLogin(request: PlayerConnectionConfiguration) {
        runBlocking {
            PlayerLoginRequestHandler(
                request,
                messageChannelManager,
                nodeService,
                playerFactory,
                playerService
            ).handle()
        }
    }

    override fun handlePostLogin(request: PlayerConnectionConfiguration) {
        PlayerPostLoginRequestHandler(request, this.selfProcessProvider, this.onlineCountProvider).handle()
    }

    override fun handleDisconnect(request: PlayerConnectionConfiguration) {
        PlayerDisconnectRequestHandler(
            request,
            this.selfProcessProvider,
            this.onlineCountProvider,
            this.igniteCloudPlayerRepository
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