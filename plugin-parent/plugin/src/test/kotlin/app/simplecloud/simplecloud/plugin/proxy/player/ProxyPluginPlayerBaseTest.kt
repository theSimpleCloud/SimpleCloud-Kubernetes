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
import app.simplecloud.simplecloud.plugin.PlayerTestHelper
import app.simplecloud.simplecloud.plugin.proxy.ProxyPluginBaseTest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach

/**
 * Date: 30.05.22
 * Time: 19:46
 * @author Frederick Baier
 *
 */
open class ProxyPluginPlayerBaseTest : ProxyPluginBaseTest() {

    private lateinit var playerTestHelper: PlayerTestHelper

    @BeforeEach
    override fun setUp() {
        super.setUp()
        this.playerTestHelper = PlayerTestHelper(
            this.pluginCloudAPI,
            this.proxyController,
            this.pluginCloudAPI.getLocalNetworkComponentName(),
            this.selfOnlineCountProvider
        )
    }

    protected fun insertPlayerWithPermissionInDatabase(permissionString: String) {
        DefaultPlayerProvider.insertPlayerWithPermission(
            this.nodeCloudAPI.getResourceRequestHandler(),
            permissionString
        )
    }

    protected fun executePlayerLogin() = runBlocking {
        playerTestHelper.executePlayerLogin()
    }

    protected fun executePlayerLogout() = runBlocking {
        playerTestHelper.executePlayerLogout()
    }

    protected fun assertSelfProcessOnlineCount(count: Int) {
        playerTestHelper.assertSelfProcessOnlineCount(count)
    }

    protected fun assertProcessOnlineCount(processName: String, count: Int) {
        playerTestHelper.assertProcessOnlineCount(processName, count)
    }

    protected fun executeConnect(serviceName: String) = runBlocking {
        playerTestHelper.executeConnect(serviceName)
    }

    protected fun assertPlayerCurrentServer(processName: String) {
        playerTestHelper.assertPlayerCurrentServer(processName)
    }

}