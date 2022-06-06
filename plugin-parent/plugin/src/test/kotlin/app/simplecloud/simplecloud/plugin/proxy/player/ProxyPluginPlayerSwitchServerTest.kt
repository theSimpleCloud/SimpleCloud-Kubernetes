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

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Date: 03.06.22
 * Time: 18:39
 * @author Frederick Baier
 *
 */
class ProxyPluginPlayerSwitchServerTest : ProxyPluginPlayerBaseTest() {

    @BeforeEach
    override fun setUp() {
        super.setUp()
        givenLobbyGroup("Lobby")
        givenOnlineProcess("Lobby", 1)
        givenServerGroup("BedWars")
        givenOnlineProcess("BedWars", 1)
        executeLoginAndConnect("Lobby-1")
    }

    @Test
    fun test() {
        executeConnect("BedWars-1")
        assertPlayerCurrentServer("BedWars-1")
    }

    private fun executeLoginAndConnect(serverName: String) {
        executePlayerLogin()
        executeConnect(serverName)
    }

}