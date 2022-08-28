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

package app.simplecloud.simplecloud.plugin.proxy.player

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.permission.configuration.PermissionConfiguration
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.node.DefaultPlayerProvider
import app.simplecloud.simplecloud.plugin.proxy.ProxyController
import app.simplecloud.simplecloud.plugin.proxy.request.ServerKickRequest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Date: 03.06.22
 * Time: 18:36
 * @author Frederick Baier
 *
 */
class ProxyPluginPlayerKickTest : ProxyPluginPlayerBaseTest() {

    @BeforeEach
    override fun setUp() {
        super.setUp()
        givenLobbyGroup("Lobby") {
            setMaxPlayers(1)
        }
        givenOnlineGroupProcesses("Lobby", 1)
        executePlayerLogin()
        executeConnect("Lobby-1")
    }

    @Test
    fun givenOneLobby_playerKickFromLobby_willThrowNoLobbyServerFound() {
        Assertions.assertThrows(ProxyController.NoLobbyServerFoundException::class.java) {
            executeKick("Lobby-1")
        }
    }

    @Test
    fun givenTwoLobbies_playerKickFromLobby_willConnectToOtherLobby() {
        givenOnlineGroupProcesses("Lobby", 1)
        val response = executeKick("Lobby-1")
        Assertions.assertEquals("Lobby-2", response.targetProcessName)
    }

    @Test
    fun givenOneLobbyAndOneVIPLobby_playerKickFromLobby_willThrowNoLobbyFound() {
        givenLobbyGroup("LobbyVIP") {
            setJoinPermission("cloud.join")
        }
        givenOnlineGroupProcesses("LobbyVIP", 1)
        Assertions.assertThrows(ProxyController.NoLobbyServerFoundException::class.java) {
            executeKick("Lobby-1")
        }
    }

    @Test
    fun givenOneLobbyAndOneVIPLobby_playerKickFromLobbyWithJoinPermission_willConnectToVIPLobby() {
        givenLobbyGroup("LobbyVIP") {
            setJoinPermission("cloud.join")
        }
        givenOnlineGroupProcesses("LobbyVIP", 1)
        addPermissionToOnlineDefaultPlayer("cloud.join")
        val response = executeKick("Lobby-1")
        Assertions.assertEquals("LobbyVIP-1", response.targetProcessName)
    }

    private fun addPermissionToOnlineDefaultPlayer(permission: String) = runBlocking {
        val playerUpdateRequest = getDefaultPlayer().createUpdateRequest()
        val permissionFactory = nodeCloudAPI.getPermissionFactory()
        playerUpdateRequest.addPermission(
            permissionFactory.create(
                PermissionConfiguration(
                    permission,
                    true,
                    -1L,
                    null
                )
            )
        )
        playerUpdateRequest.submit().await()
    }

    private suspend fun getDefaultPlayer(): CloudPlayer {
        return this.nodeCloudAPI.getCloudPlayerService()
            .findOnlinePlayerByUniqueId(DefaultPlayerProvider.DEFAULT_PLAYER_UUID).await()
    }

    private fun executeKick(serverKickedFrom: String) = runBlocking {
        proxyController.handleServerKick(
            ServerKickRequest(
                DefaultPlayerProvider.DEFAULT_PLAYER_UUID,
                "Kicked",
                serverKickedFrom
            )
        )
    }

}