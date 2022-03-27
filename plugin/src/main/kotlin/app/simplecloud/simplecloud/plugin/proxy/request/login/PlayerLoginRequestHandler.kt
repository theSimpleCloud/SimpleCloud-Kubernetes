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