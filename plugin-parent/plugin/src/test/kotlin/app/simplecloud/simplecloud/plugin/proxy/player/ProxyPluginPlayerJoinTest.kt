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

import app.simplecloud.simplecloud.node.DefaultPlayerProvider
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Date: 29.05.22
 * Time: 19:56
 * @author Frederick Baier
 *
 */
class ProxyPluginPlayerJoinTest : ProxyPluginPlayerBaseTest() {

    //most of the things are already covered in NodeAPIPlayerLoginTest

    @Test
    fun noPlayerJoin_onlineCountWillBe0() {
        assertSelfProcessOnlineCount(0)
    }

    @Test
    fun playerJoin_onlineCountWillBe1() {
        executePlayerLogin()
        assertSelfProcessOnlineCount(1)
        assertPlayerRegisteredOnProxy()
    }

    private fun assertPlayerRegisteredOnProxy() {
        val cloudPlayer = this.pluginCloudAPI.getCloudPlayerService()
            .findOnlinePlayerByUniqueId(DefaultPlayerProvider.DEFAULT_PLAYER_UUID).join()
        Assertions.assertEquals(this.pluginCloudAPI.getLocalNetworkComponentName(), cloudPlayer.getCurrentProxyName())
    }

}