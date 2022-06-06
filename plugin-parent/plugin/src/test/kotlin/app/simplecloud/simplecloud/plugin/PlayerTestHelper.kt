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

package app.simplecloud.simplecloud.plugin

import app.simplecloud.simplecloud.api.CloudAPI
import app.simplecloud.simplecloud.node.DefaultPlayerProvider
import app.simplecloud.simplecloud.plugin.proxy.ProxyController
import app.simplecloud.simplecloud.plugin.proxy.TestSelfOnlineCountProvider
import app.simplecloud.simplecloud.plugin.proxy.request.ServerConnectedRequest
import app.simplecloud.simplecloud.plugin.proxy.request.ServerPreConnectRequest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions

/**
 * Date: 04.06.22
 * Time: 21:15
 * @author Frederick Baier
 *
 */
class PlayerTestHelper(
    private val cloudAPI: CloudAPI,
    private val proxyController: ProxyController,
    private val proxyName: String,
    private val proxyOnlineCountProvider: TestSelfOnlineCountProvider
) {

    fun executePlayerLogin() = runBlocking {
        val connectionConfiguration = DefaultPlayerProvider.createDefaultPlayerConnectionConfiguration()
        proxyController.handleLogin(connectionConfiguration)
        proxyOnlineCountProvider.addToOnlineCount(1)
        proxyController.handlePostLogin(connectionConfiguration)
    }

    fun executePlayerLogout() = runBlocking {
        proxyOnlineCountProvider.addToOnlineCount(-1)
        proxyController.handleDisconnect(DefaultPlayerProvider.DEFAULT_PLAYER_UUID)
    }

    fun assertSelfProcessOnlineCount(count: Int) {
        assertProcessOnlineCount(proxyName, count)
    }

    fun assertProcessOnlineCount(processName: String, count: Int) {
        val process = this.cloudAPI.getProcessService().findByName(processName).join()
        Assertions.assertEquals(count, process.getOnlinePlayers())
    }

    fun executeConnect(serviceName: String) = runBlocking {
        val playerConnection = DefaultPlayerProvider.createDefaultPlayerConnectionConfiguration()
        proxyController.handleServerPreConnect(ServerPreConnectRequest(playerConnection, null, serviceName))
        proxyController.handleServerConnected(ServerConnectedRequest(playerConnection, serviceName))
    }

    fun assertPlayerCurrentServer(processName: String) {
        val cloudPlayer = this.cloudAPI.getCloudPlayerService()
            .findOnlinePlayerByUniqueId(DefaultPlayerProvider.DEFAULT_PLAYER_UUID).join()
        Assertions.assertEquals(processName, cloudPlayer.getCurrentServerName())
    }

}