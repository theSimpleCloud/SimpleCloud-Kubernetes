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

package app.simplecloud.simplecloud.plugin.proxy.request.login

import app.simplecloud.simplecloud.api.impl.player.CloudPlayerFactory
import app.simplecloud.simplecloud.api.messagechannel.manager.MessageChannelManager
import app.simplecloud.simplecloud.api.node.Node
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.player.configuration.CloudPlayerConfiguration
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.api.service.CloudPlayerService
import app.simplecloud.simplecloud.api.service.NodeService

class PlayerLoginRequestHandler(
    private val request: PlayerConnectionConfiguration,
    private val messageChannelManager: MessageChannelManager,
    private val nodeService: NodeService,
    private val playerFactory: CloudPlayerFactory,
    private val playerService: CloudPlayerService
) {

    private val messageChannel = this.messageChannelManager
        .getOrCreateMessageChannel<PlayerConnectionConfiguration, CloudPlayerConfiguration>("internal_player_login")

    fun handle(): CloudPlayer {
        checkPlayerAlreadyConnected()
        return createNewPlayer()
    }

    private fun checkPlayerAlreadyConnected() {
        if (doesPlayerAlreadyExist()) {
            throw PlayerAlreadyRegisteredException(this.request)
        }
    }

    private fun doesPlayerAlreadyExist(): Boolean {
        return runCatching { this.playerService.findOnlinePlayerByUniqueId(this.request.uniqueId).join() }.isSuccess
    }

    private fun createNewPlayer(): CloudPlayer {
        val randomNode = selectRandomNode()
        val playerConfiguration = this.messageChannel.createMessageRequest(request, randomNode).submit().join()
        return this.playerFactory.create(playerConfiguration)
    }

    private fun selectRandomNode(): Node {
        return this.nodeService.findFirst().join()
    }

    class PlayerAlreadyRegisteredException(
        configuration: PlayerConnectionConfiguration
    ) : Exception("Player ${configuration.name} (${configuration.uniqueId}) is already registered")


}