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

import app.simplecloud.simplecloud.api.internal.InternalCloudAPI
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.service.CloudPlayerService
import app.simplecloud.simplecloud.node.util.TestPlayerProvider
import app.simplecloud.simplecloud.node.util.TestProcessProvider
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Date: 28.08.22
 * Time: 11:37
 * @author Frederick Baier
 *
 */
abstract class OfflinePlayerGetTest : TestProcessProvider, TestPlayerProvider {

    protected lateinit var playerService: CloudPlayerService

    abstract override fun getCloudAPI(): InternalCloudAPI

    @BeforeEach
    open fun setUp() {
        this.playerService = getCloudAPI().getCloudPlayerService()
    }

    @Test
    fun playerNotInDatabase_findOfflinePlayer_WillFail(): Unit = runBlocking {
        assertThrows(NoSuchElementException::class.java) {
            findDefaultOfflinePlayer()
        }
    }

    @Test
    fun onlinePlayer_finOfflinePlayer_willReturnOnlinePlayer(): Unit = runBlocking {
        givenProxyGroup("Proxy")
        givenOnlineGroupProcesses("Proxy", 1)
        executeLoginOnProxy1WithDefaultPlayer()

        assertTrue(findDefaultOfflinePlayer() is CloudPlayer)
    }

    @Test
    fun playerInDatabase_finOfflinePlayer_willReturnNormalOfflinePlayer(): Unit = runBlocking {
        insertPlayerInDatabase()

        assertTrue(findDefaultOfflinePlayer() !is CloudPlayer)
    }

    @Test
    fun playerLoginAndLogout_finOfflinePlayer_willReturnNormalOfflinePlayer(): Unit = runBlocking {
        givenProxyGroup("Proxy")
        givenOnlineGroupProcesses("Proxy", 1)
        executeLoginOnProxy1WithDefaultPlayer()
        executeLogoutOnDefaultPlayer()

        assertTrue(findDefaultOfflinePlayer() !is CloudPlayer)
    }

}