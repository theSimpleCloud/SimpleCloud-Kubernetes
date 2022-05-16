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

package app.simplecloud.simplecloud.node.api.player

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.internal.configutation.PlayerLoginConfiguration
import app.simplecloud.simplecloud.api.permission.Permissions
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.node.player.CloudPlayerLoginHandler
import app.simplecloud.simplecloud.node.player.CloudPlayerLoginJoinPermissionChecker
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Date: 15.05.22
 * Time: 19:16
 * @author Frederick Baier
 *
 */
class NodeAPIPlayerLoginTest : NodeAPIPlayerTest() {

    @Test
    fun playerLoginOnUnregisteredGroup_willThrow() {
        Assertions.assertThrows(CloudPlayerLoginHandler.UnknownProxyProcessException::class.java) {
            executeLoginOnProxy1WithDefaultPlayer()
        }
    }

    @Test
    fun playerLoginOnUnregisteredProcess_willThrow() {
        givenProxyGroup("Proxy", 20, false, null)
        Assertions.assertThrows(CloudPlayerLoginHandler.UnknownProxyProcessException::class.java) {
            executeLoginOnProxy1WithDefaultPlayer()
        }
    }

    @Test
    fun playerLoginOnRegisteredProcessWith0MaxPlayers_willThrow() {
        givenProxyGroup("Proxy", 0, false, null)
        givenProcesses("Proxy", 1)
        Assertions.assertThrows(CloudPlayerLoginJoinPermissionChecker.ProxyFullException::class.java) {
            executeLoginOnProxy1WithDefaultPlayer()
        }
    }

    @Test
    fun playerLoginOnRegisteredProcessWithMaintenance_willThrow() {
        givenProxyGroup("Proxy", 20, true, null)
        givenProcesses("Proxy", 1)
        Assertions.assertThrows(CloudPlayerLoginJoinPermissionChecker.ProxyMaintenanceException::class.java) {
            executeLoginOnProxy1WithDefaultPlayer()
        }
    }

    @Test
    fun playerLoginOnRegisteredProcessWithJoinPermission_willThrow() {
        givenProxyGroup("Proxy", 20, false, "proxy.join")
        givenProcesses("Proxy", 1)
        Assertions.assertThrows(CloudPlayerLoginJoinPermissionChecker.NoJoinPermissionException::class.java) {
            executeLoginOnProxy1WithDefaultPlayer()
        }
    }

    @Test
    fun playerLoginOnRegisteredProcess() {
        givenProxyGroup("Proxy", 20, false, null)
        givenProcesses("Proxy", 1)
        val player = executeLoginOnProxy1WithDefaultPlayer()
        Assertions.assertEquals("Proxy-1", player.getCurrentProxyName())
        Assertions.assertEquals(null, player.getCurrentServerName())
    }

    @Test
    fun playerLoginWithMaintenanceEnabled_playerLoginWithMaintenancePermission_willNotFail() {
        givenProxyGroup("Proxy", 20, true, null)
        givenProcesses("Proxy", 1)
        executeLoginOnProxy1WithPermission(Permissions.MAINTENANCE_JOIN)
    }

    @Test
    fun playerLoginWithJoinPermissionEnabled_playerLoginWithJoinPermission_willNotFail() {
        givenProxyGroup("Proxy", 20, false, "proxy.join")
        givenProcesses("Proxy", 1)
        executeLoginOnProxy1WithPermission("proxy.join")
    }

    @Test
    fun playerLoginOnFullProxy_playerLoginWithJoinFullPermission_willNotFail() {
        givenProxyGroup("Proxy", 0, false, null)
        givenProcesses("Proxy", 1)
        executeLoginOnProxy1WithPermission(Permissions.JOIN_FULL)
    }

    @Test
    fun afterFirstLogin_playerWillBeSavedInDatabase() {
        givenProxyGroup("Proxy", 20, false, null)
        givenProcesses("Proxy", 1)
        executeLoginOnProxy1WithDefaultPlayer()
        assertDefaultPlayerInDatabase()
    }

    private fun assertDefaultPlayerInDatabase() {
        val repository = this.databaseFactory.offlineCloudPlayerRepository
        runBlocking {
            repository.find(DefaultPlayerProvider.DEFAULT_PLAYER_UUID).await()
        }
    }

    private fun executeLoginOnProxy1WithPermission(permissionString: String): CloudPlayer {
        insertPlayerWithPermissionInDatabase(permissionString)
        return executeLoginOnProxy1WithDefaultPlayer()
    }

    private fun executeLoginOnProxy1WithDefaultPlayer(): CloudPlayer {
        val connectionConfig = createDefaultPlayerConnectionConfiguration()
        return executePlayerLoginOnProxy1(connectionConfig)
    }

    private fun executePlayerLoginOnProxy1(config: PlayerConnectionConfiguration): CloudPlayer = runBlocking {
        return@runBlocking cloudPlayerService.loginPlayer(PlayerLoginConfiguration(config, "Proxy-1"))
    }
}