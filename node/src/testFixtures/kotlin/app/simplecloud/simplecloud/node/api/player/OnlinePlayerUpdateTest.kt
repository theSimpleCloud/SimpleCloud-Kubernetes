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
import app.simplecloud.simplecloud.api.internal.request.player.InternalCloudPlayerUpdateRequest
import app.simplecloud.simplecloud.api.permission.PermissionEntity
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.request.permission.PermissionEntityUpdateRequest
import app.simplecloud.simplecloud.node.DefaultPlayerProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

/**
 * Date: 29.08.22
 * Time: 11:23
 * @author Frederick Baier
 *
 */
abstract class OnlinePlayerUpdateTest : OfflinePlayerUpdateTest() {

    private lateinit var onlinePlayer: CloudPlayer

    abstract override fun getCloudAPI(): InternalCloudAPI

    override fun fetchDefaultPermissionEntity(): PermissionEntity {
        return findDefaultOnlinePlayer()
    }

    override fun createUpdateRequest(entity: PermissionEntity): PermissionEntityUpdateRequest {
        return (entity as CloudPlayer).createUpdateRequest()
    }

    @BeforeEach
    override fun setUp() {
        this.playerService = getCloudAPI().getCloudPlayerService()
        givenProxyGroup("Proxy")
        givenOnlineGroupProcesses("Proxy", 1)
        executeLoginOnProxy1WithDefaultPlayer()
        this.onlinePlayer = findDefaultOnlinePlayer()
        super.setUp()
    }

    @Test
    fun playerIsConnectedToProxy1() {
        assertEquals("Proxy-1", findDefaultOnlinePlayer().getCurrentProxyName())
    }

    @ParameterizedTest
    @ValueSource(strings = ["Lobby-1", "Lobby", "Gs"])
    fun updateCurrentOnlineServerTest(currentServer: String) {
        val updateRequest = this.onlinePlayer.createUpdateRequest()
        updateRequest as InternalCloudPlayerUpdateRequest
        updateRequest.setConnectedServerName(currentServer)
        updateRequest.submit().join()

        assertEquals(currentServer, findDefaultOnlinePlayer().getCurrentServerName())
    }

    @ParameterizedTest
    @ValueSource(strings = ["Lobby-1", "Lobby", "Gs"])
    fun updateCurrentProxyServer(proxyServer: String) {
        val updateRequest = this.onlinePlayer.createUpdateRequest()
        updateRequest as InternalCloudPlayerUpdateRequest
        updateRequest.setConnectedProxyName(proxyServer)
        updateRequest.submit().join()

        assertEquals(proxyServer, findDefaultOnlinePlayer().getCurrentProxyName())
    }

    private fun findDefaultOnlinePlayer(): CloudPlayer {
        return this.playerService.findOnlinePlayerByUniqueId(DefaultPlayerProvider.DEFAULT_PLAYER_UUID).join()
    }


}