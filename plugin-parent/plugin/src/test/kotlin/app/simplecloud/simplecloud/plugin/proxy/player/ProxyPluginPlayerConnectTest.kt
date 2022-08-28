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
class ProxyPluginPlayerConnectTest : ProxyPluginPlayerBaseTest() {


    @Test
    fun noLobby_playerJoin_willFail() {
        executePlayerLogin()
        Assertions.assertThrows(ProxyController.NoSuchProcessException::class.java) {
            executeConnect("Lobby-1")
        }
    }

    @Test
    fun lobbyInMaintenance_playerJoin_willFail() {
        givenLobbyGroup("Lobby") {
            setMaintenance(true)
        }
        givenGroupProcesses("Lobby", 1)
        executePlayerLogin()
        Assertions.assertThrows(ProxyController.NoPermissionToJoinGroupException::class.java) {
            executeConnect("Lobby-1")
        }
    }

    @Test
    fun lobbyWithJoinPermission_playerJoin_willFail() {
        givenLobbyGroup("Lobby") {
            setJoinPermission("my.join.permission")
        }
        givenGroupProcesses("Lobby", 1)
        executePlayerLogin()
        Assertions.assertThrows(ProxyController.NoPermissionToJoinGroupException::class.java) {
            executeConnect("Lobby-1")
        }
    }

    @Test
    fun lobbyNotOnline_playerJoin_willFail() {
        givenLobbyGroup("Lobby") {
            setMaxPlayers(1)
        }
        givenGroupProcesses("Lobby", 1)
        executePlayerLogin()
        Assertions.assertThrows(ProxyController.ProcessNotJoinableException::class.java) {
            executeConnect("Lobby-1")
        }
    }

    @Test
    fun lobbyWith0MaxPlayers_playerJoin_willFail() {
        givenLobbyGroup("Lobby") {
            setMaxPlayers(0)
        }
        givenOnlineGroupProcesses("Lobby", 1)
        executePlayerLogin()
        Assertions.assertThrows(ProxyController.ProcessFullException::class.java) {
            executeConnect("Lobby-1")
        }
    }

    @Test
    fun lobbyWith1MaxPlayers_playerJoin_willWork() {
        givenLobbyGroup("Lobby") {
            setMaxPlayers(1)
        }
        givenOnlineGroupProcesses("Lobby", 1)

        executePlayerLogin()
        executeConnect("Lobby-1")

        assertSelfProcessOnlineCount(1)
        assertPlayerCurrentServer("Lobby-1")
    }


    @Test
    fun lobbyWith1MaxPlayers_playerJoinOnFallback_willWork() {
        givenLobbyGroup("Lobby") {
            setMaxPlayers(1)
        }
        givenOnlineGroupProcesses("Lobby", 1)

        executePlayerLogin()
        executeConnect("fallback")

        assertSelfProcessOnlineCount(1)
        assertPlayerCurrentServer("Lobby-1")
    }

    @Test
    fun givenProxy_playerConnectToProxy_willFail() {
        givenProxyGroup("TestProxy")
        givenOnlineGroupProcesses("TestProxy", 1)

        executePlayerLogin()

        Assertions.assertThrows(ProxyController.IllegalGroupTypeException::class.java) {
            executeConnect("TestProxy-1")
        }
    }

}