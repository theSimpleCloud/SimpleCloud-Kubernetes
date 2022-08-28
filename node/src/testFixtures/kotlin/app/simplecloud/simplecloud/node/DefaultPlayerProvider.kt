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

package app.simplecloud.simplecloud.node

import app.simplecloud.simplecloud.api.permission.configuration.PermissionConfiguration
import app.simplecloud.simplecloud.api.permission.configuration.PermissionPlayerConfiguration
import app.simplecloud.simplecloud.api.player.PlayerWebConfig
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.database.memory.factory.InMemoryRepositorySafeDatabaseFactory
import app.simplecloud.simplecloud.distribution.api.Address
import java.util.*

/**
 * Date: 16.05.22
 * Time: 12:38
 * @author Frederick Baier
 *
 */
object DefaultPlayerProvider {

    const val DEFAULT_PLAYER_NAME = "Fllip"

    val DEFAULT_PLAYER_UUID = UUID.randomUUID()

    fun insertPlayerWithPermission(databaseFactory: InMemoryRepositorySafeDatabaseFactory, permissionString: String) {
        insertPlayerInDatabase(
            databaseFactory,
            createOfflineDefaultPlayer(
                listOf(createPermission(permissionString))
            )
        )
    }

    fun insertPlayerInDatabase(databaseFactory: InMemoryRepositorySafeDatabaseFactory) {
        insertPlayerInDatabase(
            databaseFactory,
            createOfflineDefaultPlayer(emptyList())
        )
    }

    private fun insertPlayerInDatabase(
        databaseFactory: InMemoryRepositorySafeDatabaseFactory,
        player: OfflineCloudPlayerConfiguration,
    ) {
        val offlineCloudPlayerRepository = databaseFactory.offlineCloudPlayerRepository
        offlineCloudPlayerRepository.save(
            player.uniqueId,
            player
        )
    }

    private fun createOfflineDefaultPlayer(
        permissionList: List<PermissionConfiguration> = emptyList()
    ): OfflineCloudPlayerConfiguration {
        return OfflineCloudPlayerConfiguration(
            DEFAULT_PLAYER_NAME,
            DEFAULT_PLAYER_UUID,
            System.currentTimeMillis(),
            System.currentTimeMillis(),
            0L,
            DEFAULT_PLAYER_NAME,
            createDefaultPlayerConnectionConfiguration(),
            PlayerWebConfig("", false),
            PermissionPlayerConfiguration(
                DEFAULT_PLAYER_UUID,
                permissionList
            )
        )
    }

    fun createDefaultPlayerConnectionConfiguration(): PlayerConnectionConfiguration {
        return PlayerConnectionConfiguration(
            DEFAULT_PLAYER_UUID,
            0,
            DEFAULT_PLAYER_NAME,
            Address("127.0.0.1", 5645),
            true
        )
    }

    private fun createPermission(permissionString: String): PermissionConfiguration {
        return PermissionConfiguration(
            permissionString,
            true,
            -1,
            null
        )
    }

}