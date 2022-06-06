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

import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.plugin.proxy.request.*
import java.util.*

interface ProxyController {

    suspend fun handleLogin(request: PlayerConnectionConfiguration): CloudPlayer

    suspend fun handlePostLogin(request: PlayerConnectionConfiguration)

    suspend fun handleDisconnect(playerUniqueId: UUID)

    /**
     * @throws NoLobbyServerFoundException no lobby server was found
     * @throws NoPermissionToJoinGroupException the player doesn't have the permission to join processes of the group
     * @throws IllegalGroupTypeException the group is a proxy group
     * @throws IllegalProcessStateException the process is not online and therefore cannot be joined
     * @throws NoLobbyServerFoundException if no lobby server was found
     */
    suspend fun handleServerPreConnect(request: ServerPreConnectRequest): ServerPreConnectResponse

    suspend fun handleServerConnected(request: ServerConnectedRequest)

    suspend fun handleServerKick(request: ServerKickRequest)

    class NoPermissionToJoinGroupException() : Exception("No Permission to join group")

    class IllegalGroupTypeException() : Exception("Cannot connect to groups with that type")

    class ProcessNotJoinableException() : Exception("Process not joinable")

    class ProcessFullException() : Exception("Process is full")

    class NoSuchProcessException() : Exception("Cannot find requested process")

    class NoLobbyServerFoundException() : Exception("No Lobby-Server found")

}