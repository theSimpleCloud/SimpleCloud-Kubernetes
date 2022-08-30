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
import app.simplecloud.simplecloud.api.permission.PermissionEntity
import app.simplecloud.simplecloud.api.player.OfflineCloudPlayer
import app.simplecloud.simplecloud.api.player.PlayerWebConfig
import app.simplecloud.simplecloud.api.request.permission.PermissionEntityUpdateRequest
import app.simplecloud.simplecloud.api.service.CloudPlayerService
import app.simplecloud.simplecloud.node.api.permission.PermissionEntityUpdateBaseTest
import app.simplecloud.simplecloud.node.util.TestPlayerProvider
import app.simplecloud.simplecloud.node.util.TestProcessProvider
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

/**
 * Date: 29.08.22
 * Time: 11:23
 * @author Frederick Baier
 *
 */
abstract class OfflinePlayerUpdateTest : PermissionEntityUpdateBaseTest(), TestProcessProvider, TestPlayerProvider {

    protected lateinit var playerService: CloudPlayerService

    abstract override fun getCloudAPI(): InternalCloudAPI

    override fun fetchDefaultPermissionEntity(): PermissionEntity {
        return findDefaultOfflinePlayer()
    }

    override fun createUpdateRequest(entity: PermissionEntity): PermissionEntityUpdateRequest {
        return (entity as OfflineCloudPlayer).createUpdateRequest()
    }

    @BeforeEach
    override fun setUp() {
        this.playerService = getCloudAPI().getCloudPlayerService()
        insertPlayerInDatabase()
        super.setUp()
    }

    @ParameterizedTest
    @ValueSource(strings = ["MyString", "", "+#"])
    fun updateDisplayNameTest(displayName: String) {
        val updateRequest = findDefaultOfflinePlayer().createUpdateRequest()
        updateRequest.setDisplayName(displayName)
        updateRequest.submit().join()

        Assertions.assertEquals(displayName, findDefaultOfflinePlayer().getDisplayName())
    }

    @ParameterizedTest
    @MethodSource("webConfigs")
    fun updateWebConfigTest(webConfig: PlayerWebConfig) {
        val updateRequest = findDefaultOfflinePlayer().createUpdateRequest()
        updateRequest.setWebConfig(webConfig)
        updateRequest.submit().join()

        val player = findDefaultOfflinePlayer()
        Assertions.assertEquals(webConfig.password, player.getWebConfig().password)
        Assertions.assertEquals(webConfig.hasAccess, player.getWebConfig().hasAccess)
    }

    companion object {

        @JvmStatic
        fun webConfigs(): List<PlayerWebConfig> {
            return listOf(
                PlayerWebConfig("test", false),
                PlayerWebConfig("password", true),
                PlayerWebConfig("", false),
                PlayerWebConfig("231f", true),
            )
        }

    }


}