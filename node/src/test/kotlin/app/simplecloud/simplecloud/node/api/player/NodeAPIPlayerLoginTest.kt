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
import app.simplecloud.simplecloud.api.internal.exception.PlayerAlreadyRegisteredException
import app.simplecloud.simplecloud.api.internal.exception.UnknownProxyProcessException
import app.simplecloud.simplecloud.api.permission.Permissions
import app.simplecloud.simplecloud.node.DefaultPlayerProvider
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
        Assertions.assertThrows(UnknownProxyProcessException::class.java) {
            executeLoginOnProxy1WithDefaultPlayer()
        }
    }

    @Test
    fun playerLoginOnUnregisteredProcess_willThrow() {
        givenProxyGroup("Proxy")
        Assertions.assertThrows(UnknownProxyProcessException::class.java) {
            executeLoginOnProxy1WithDefaultPlayer()
        }
    }

    @Test
    fun playerLoginOnRegisteredProcessWith0MaxPlayers_willThrow() {
        givenProxyGroup("Proxy") {
            setMaxPlayers(0)
        }
        givenOnlineGroupProcesses("Proxy", 1)
        Assertions.assertThrows(CloudPlayerLoginJoinPermissionChecker.ProxyFullException::class.java) {
            executeLoginOnProxy1WithDefaultPlayer()
        }
    }

    @Test
    fun playerLoginOnRegisteredProcessWithMaintenance_willThrow() {
        givenProxyGroup("Proxy") {
            setMaintenance(true)
        }
        givenOnlineGroupProcesses("Proxy", 1)
        Assertions.assertThrows(CloudPlayerLoginJoinPermissionChecker.ProxyMaintenanceException::class.java) {
            executeLoginOnProxy1WithDefaultPlayer()
        }
    }

    @Test
    fun playerLoginOnRegisteredProcessWithJoinPermission_willThrow() {
        givenProxyGroup("Proxy") {
            setJoinPermission("proxy.join")
        }
        givenOnlineGroupProcesses("Proxy", 1)
        Assertions.assertThrows(CloudPlayerLoginJoinPermissionChecker.NoJoinPermissionException::class.java) {
            executeLoginOnProxy1WithDefaultPlayer()
        }
    }

    @Test
    fun playerLoginOnRegisteredProcess() {
        givenProxyGroup("Proxy")
        givenOnlineGroupProcesses("Proxy", 1)
        val player = executeLoginOnProxy1WithDefaultPlayer()
        Assertions.assertEquals("Proxy-1", player.getCurrentProxyName())
        Assertions.assertEquals(null, player.getCurrentServerName())
    }

    @Test
    fun playerLoginWithMaintenanceEnabled_playerLoginWithMaintenancePermission_willNotFail() {
        givenProxyGroup("Proxy") {
            setMaintenance(true)
        }
        givenOnlineGroupProcesses("Proxy", 1)
        executeLoginOnProxy1WithPermission(Permissions.MAINTENANCE_JOIN)
    }

    @Test
    fun playerLoginWithJoinPermissionEnabled_playerLoginWithJoinPermission_willNotFail() {
        givenProxyGroup("Proxy") {
            setJoinPermission("proxy.join")
        }
        givenOnlineGroupProcesses("Proxy", 1)
        executeLoginOnProxy1WithPermission("proxy.join")
    }

    @Test
    fun playerLoginOnFullProxy_playerLoginWithJoinFullPermission_willNotFail() {
        givenProxyGroup("Proxy") {
            setMaxPlayers(0)
        }
        givenOnlineGroupProcesses("Proxy", 1)
        executeLoginOnProxy1WithPermission(Permissions.JOIN_FULL)
    }

    @Test
    fun afterLogin_playerWillBeInCache() {
        givenProxyGroup("Proxy")
        givenOnlineGroupProcesses("Proxy", 1)
        executeLoginOnProxy1WithDefaultPlayer()
        runBlocking {
            cloudPlayerService.findOnlinePlayerByUniqueId(DefaultPlayerProvider.DEFAULT_PLAYER_UUID).await()
        }
    }

    @Test
    fun afterFirstLogin_playerWillBeSavedInDatabase() {
        givenProxyGroup("Proxy")
        givenOnlineGroupProcesses("Proxy", 1)
        executeLoginOnProxy1WithDefaultPlayer()
        assertDefaultPlayerInDatabase()
    }

    @Test
    fun executeLoginTwice_willFail() {
        givenProxyGroup("Proxy")
        givenOnlineGroupProcesses("Proxy", 1)
        executeLoginOnProxy1WithDefaultPlayer()

        Assertions.assertThrows(PlayerAlreadyRegisteredException::class.java) {
            executeLoginOnProxy1WithDefaultPlayer()
        }

    }

    private fun assertDefaultPlayerInDatabase() {
        val repository = this.databaseFactory.offlineCloudPlayerRepository
        runBlocking {
            Assertions.assertTrue(repository.doesExist(DefaultPlayerProvider.DEFAULT_PLAYER_UUID).await())
        }
    }
}