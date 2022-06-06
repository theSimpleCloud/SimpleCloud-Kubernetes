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

package app.simplecloud.simplecloud.plugin.combined

import app.simplecloud.simplecloud.plugin.*
import app.simplecloud.simplecloud.plugin.proxy.TestSelfOnlineCountProvider
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Date: 03.06.22
 * Time: 21:02
 * @author Frederick Baier
 *
 */
class TwoPluginPlayerTest : PluginBaseTest() {

    private var lobbyOnlineCountProvider: TestSelfOnlineCountProvider = TestSelfOnlineCountProvider()
    private var proxyOnlineCountProvider: TestSelfOnlineCountProvider = TestSelfOnlineCountProvider()

    private lateinit var playerTestHelper: PlayerTestHelper

    @BeforeEach
    override fun setUp() {
        super.setUp()
        givenProxyGroup("Proxy")
        givenLobbyGroup("Lobby")
        givenProcess("Proxy", 1)
        givenProcess("Lobby", 1)
        val serverPlugin = startServerPlugin("Lobby-1")
        val proxyPlugin = startProxyPlugin("Proxy-1")
        this.lobbyOnlineCountProvider = serverPlugin.testSelfOnlineCountProvider
        this.proxyOnlineCountProvider = proxyPlugin.testSelfOnlineCountProvider
        val cloudProxyPlugin = proxyPlugin.plugin
        this.playerTestHelper = PlayerTestHelper(
            this.nodeCloudAPI,
            cloudProxyPlugin.proxyController,
            cloudProxyPlugin.cloudAPI.getLocalNetworkComponentName(),
            this.proxyOnlineCountProvider
        )
    }

    @Test
    fun playerLogin_onlineCountWillBe1() {
        this.playerTestHelper.executePlayerLogin()
        executePlayerConnect()

        Assertions.assertEquals(1, this.proxyOnlineCountProvider.getOnlineCount())
        Assertions.assertEquals(1, this.lobbyOnlineCountProvider.getOnlineCount())
    }

    @Test
    fun playerLoginAndLogout_onlineCountWillBe0() {
        this.playerTestHelper.executePlayerLogin()
        executePlayerConnect()
        executePlayerLogout()

        Assertions.assertEquals(0, this.proxyOnlineCountProvider.getOnlineCount())
        Assertions.assertEquals(0, this.lobbyOnlineCountProvider.getOnlineCount())
    }

    private fun executePlayerLogout() {
        this.playerTestHelper.executePlayerLogout()
        this.lobbyOnlineCountProvider.addToOnlineCount(-1)
    }

    private fun executePlayerConnect() {
        this.playerTestHelper.executeConnect("Lobby-1")
        this.lobbyOnlineCountProvider.addToOnlineCount(1)
    }

    private fun startProxyPlugin(processName: String): ProxyPluginStarter.ProxyPluginConfig {
        return createTestCloudPluginStarter(processName).createProxyPlugin()
    }

    private fun startServerPlugin(processName: String): ServerPluginStarter.ServerPluginConfig {
        return createTestCloudPluginStarter(processName).createServerPlugin()
    }

    private fun createTestCloudPluginStarter(processName: String): TestCloudPluginStarter {
        val cloudProcess = this.nodeCloudAPI.getProcessService().findByName(processName).join()
        return TestCloudPluginStarter(this.kubeAPI, cloudProcess.getUniqueId())
    }

}
