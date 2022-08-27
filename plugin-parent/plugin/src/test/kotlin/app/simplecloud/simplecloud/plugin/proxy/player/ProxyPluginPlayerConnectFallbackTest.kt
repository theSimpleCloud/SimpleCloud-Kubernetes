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

import app.simplecloud.simplecloud.plugin.proxy.ProxyController
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Date: 03.06.22
 * Time: 18:36
 * @author Frederick Baier
 *
 */
class ProxyPluginPlayerConnectFallbackTest : ProxyPluginPlayerBaseTest() {


    @Test
    fun noLobby_playerJoin_willNotFindLobby() {
        Assertions.assertThrows(ProxyController.NoLobbyServerFoundException::class.java) {
            loginAndConnectToFallback()
        }
    }

    @Test
    fun givenLobbyWith0Processes_playerJoin_willNotFindLobby() {
        givenLobbyGroup("Lobby")

        Assertions.assertThrows(ProxyController.NoLobbyServerFoundException::class.java) {
            loginAndConnectToFallback()
        }
    }

    @Test
    fun givenLobbyInMaintenance_playerJoin_willNotFindLobby() {
        givenLobbyGroup("Lobby") {
            setMaintenance(true)
        }
        givenOnlineProcess("Lobby", 1)

        Assertions.assertThrows(ProxyController.NoLobbyServerFoundException::class.java) {
            loginAndConnectToFallback()
        }
    }

    @Test
    fun givenLobbyWithJoinPermission_playerJoin_willNotFindLobby() {
        givenLobbyGroup("Lobby") {
            setJoinPermission("cloud.join")
        }
        givenOnlineProcess("Lobby", 1)

        Assertions.assertThrows(ProxyController.NoLobbyServerFoundException::class.java) {
            loginAndConnectToFallback()
        }
    }

    @Test
    fun givenLobbyWith0MaxPlayers_playerJoin_willNotFindLobby() {
        givenLobbyGroup("Lobby") {
            setMaxPlayers(0)
        }
        givenOnlineProcess("Lobby", 1)

        Assertions.assertThrows(ProxyController.NoLobbyServerFoundException::class.java) {
            loginAndConnectToFallback()
        }
    }

    @Test
    fun givenLobby_playerJoin_willFindLobby() {
        givenLobbyGroup("Lobby") {
            setMaxPlayers(1)
        }
        givenOnlineProcess("Lobby", 1)

        loginAndConnectToFallback()

        assertPlayerCurrentServer("Lobby-1")
    }

    @Test
    fun givenLobbyAndHigherPriorityLobby_playerJoin_willConnectToHigherPriorityLobby() {
        givenLobbyGroup("VIP-Lobby") {
            setLobbyPriority(10)
        }
        givenLobbyGroup("Lobby")
        givenOnlineProcess("VIP-Lobby", 1)
        givenOnlineProcess("Lobby", 1)

        loginAndConnectToFallback()

        assertPlayerCurrentServer("VIP-Lobby-1")
    }

    @Test
    fun givenLobbyAndHigherPriorityLobbyButHigherPriorityLobbyIsOffline_playerJoin_willConnectToNormalLobby() {
        givenLobbyGroup("VIP-Lobby") {
            setLobbyPriority(10)
        }
        givenLobbyGroup("Lobby")
        givenOnlineProcess("Lobby", 1)

        loginAndConnectToFallback()

        assertPlayerCurrentServer("Lobby-1")
    }

    @Test
    fun givenLobbyAndVipLobby_playerJoinWithWrongPermission_willJoinNormalLobby() {
        givenLobbyGroup("VIP-Lobby") {
            setJoinPermission("lobby.vip.join")
            setLobbyPriority(10)
        }
        givenLobbyGroup("Lobby")
        givenOnlineProcess("VIP-Lobby", 1)
        givenOnlineProcess("Lobby", 1)

        loginAndConnectToFallbackWithPermission("wrong.permission")

        assertPlayerCurrentServer("Lobby-1")
    }

    @Test
    fun givenOnlyVipLobby_playerJoin_willFail() {
        givenLobbyGroup("VIP-Lobby") {
            setJoinPermission("lobby.vip.join")
            setLobbyPriority(10)
        }
        givenOnlineProcess("VIP-Lobby", 1)


        Assertions.assertThrows(ProxyController.NoLobbyServerFoundException::class.java) {
            loginAndConnectToFallback()
        }
    }

    @Test
    fun givenLobbyAndVipLobby_playerJoinWithPermission_willJoinVipLobby() {
        givenLobbyGroup("VIP-Lobby") {
            setJoinPermission("lobby.vip.join")
            setLobbyPriority(10)
        }
        givenLobbyGroup("Lobby")
        givenOnlineProcess("VIP-Lobby", 1)
        givenOnlineProcess("Lobby", 1)

        loginAndConnectToFallbackWithPermission("lobby.vip.join")

        assertPlayerCurrentServer("VIP-Lobby-1")
    }

    @Test
    fun givenTwoLobbiesOneWithHigherPriority_playerJoin_willAlwaysJoinHigherPriorityLobby() {
        givenLobbyGroup("Higher-Lobby") {
            setLobbyPriority(10)
        }
        givenLobbyGroup("Lobby")
        givenOnlineProcess("Higher-Lobby", 1)
        givenOnlineProcess("Lobby", 1)


        loginAndConnectToFallback()

        assertPlayerCurrentServer("Higher-Lobby-1")
    }

    @Test
    fun givenStaticLobby_playerJoin_willConnectToStaticLobby() {
        givenStaticLobbyTemplate("StaticLobby")
        changeStateOfStaticProcessToOnline("StaticLobby")

        loginAndConnectToFallback()

        assertPlayerCurrentServer("StaticLobby")
    }

    private fun loginAndConnectToFallbackWithPermission(permission: String) {
        insertPlayerWithPermissionInDatabase(permission)
        executePlayerLogin()
        executeConnectToFallback()
    }

    private fun loginAndConnectToFallback() {
        executePlayerLogin()
        executeConnectToFallback()
    }


    private fun executeConnectToFallback() {
        executeConnect("fallback")
    }

}