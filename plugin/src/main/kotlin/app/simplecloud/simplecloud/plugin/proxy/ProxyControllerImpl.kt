/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
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