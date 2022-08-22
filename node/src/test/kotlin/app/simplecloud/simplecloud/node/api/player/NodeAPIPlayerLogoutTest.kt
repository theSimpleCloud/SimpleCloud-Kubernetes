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
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.node.DefaultPlayerProvider
import kotlinx.coroutines.runBlocking
import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Date: 16.05.22
 * Time: 12:29
 * @author Frederick Baier
 *
 */
class NodeAPIPlayerLogoutTest : NodeAPIPlayerTest() {

    private lateinit var loggedInPlayer: CloudPlayer

    @BeforeEach
    override fun setUp(): Unit = runBlocking {
        super.setUp()
        givenProxyGroup("Proxy", 20, false, null)
        givenProcesses("Proxy", 1)
        val configuration = PlayerLoginConfiguration(createDefaultPlayerConnectionConfiguration(), "Proxy-1")
        loggedInPlayer = cloudPlayerService.loginPlayer(configuration)
    }

    @Test
    fun doNothing_playerWillBeInCache() {
        runBlocking {
            cloudPlayerService.findOnlinePlayerByUniqueId(DefaultPlayerProvider.DEFAULT_PLAYER_UUID).await()
        }
    }

    @Test
    fun doLogout_playerWillNoLongerBeInCache(): Unit = runBlocking {
        executeLogout()
        Assertions.assertThrows(NoSuchElementException::class.java) {
            runBlocking {
                cloudPlayerService.findOnlinePlayerByUniqueId(DefaultPlayerProvider.DEFAULT_PLAYER_UUID).await()
            }
        }
    }

    @Test
    fun changeDisplayNameOfPlayer_logout_displayNameWillBeSavedInDatabase(): Unit = runBlocking {
        val newDisplayName = RandomStringUtils.randomAlphabetic(16)
        loggedInPlayer.createUpdateRequest().setDisplayName(newDisplayName).submit().await()
        executeLogout()
        val playerConfiguration =
            databaseFactory.offlineCloudPlayerRepository.find(loggedInPlayer.getUniqueId()).await()
        Assertions.assertEquals(newDisplayName, playerConfiguration.displayName)
    }

    private suspend fun executeLogout() {
        cloudPlayerService.logoutPlayer(DefaultPlayerProvider.DEFAULT_PLAYER_UUID)
    }

}