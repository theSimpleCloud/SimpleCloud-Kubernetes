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

package eu.thesimplecloud.simplecloud.plugin.proxy

import eu.thesimplecloud.simplecloud.api.player.IPlayerConnection
import eu.thesimplecloud.simplecloud.plugin.proxy.request.ServerConnectedRequest
import eu.thesimplecloud.simplecloud.plugin.proxy.request.ServerKickRequest
import eu.thesimplecloud.simplecloud.plugin.proxy.request.ServerPreConnectRequest
import eu.thesimplecloud.simplecloud.plugin.proxy.request.login.PlayerLoginRequestHandler

class ProxyControllerImpl : IProxyController {

    override fun handleLogin(request: IPlayerConnection) {
        PlayerLoginRequestHandler(request).handle()
    }

    override fun handlePostLogin(request: IPlayerConnection) {
        TODO("Not yet implemented")
    }

    override fun handleDisconnect(request: IPlayerConnection) {
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