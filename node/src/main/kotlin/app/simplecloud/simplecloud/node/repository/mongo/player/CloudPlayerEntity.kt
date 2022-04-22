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

package app.simplecloud.simplecloud.node.repository.mongo.player

import app.simplecloud.simplecloud.api.permission.configuration.PermissionPlayerConfiguration
import app.simplecloud.simplecloud.api.player.PlayerWebConfig
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.distribution.api.Address
import dev.morphia.annotations.Entity
import dev.morphia.annotations.Id
import java.util.*

@Entity("v3_players")
class CloudPlayerEntity(
    @Id
    val uniqueId: UUID,
    val name: String,
    val firstLogin: Long,
    val lastLogin: Long,
    val onlineTime: Long,
    val displayName: String,
    val lastPlayerConnection: PlayerConnectionConfiguration,
    val webConfig: PlayerWebConfig,
    val permissionConfiguration: PermissionPlayerConfiguration
) {

    private constructor() : this(
        UUID.randomUUID(),
        "",
        -1,
        -1,
        -1,
        "",
        PlayerConnectionConfiguration(
            UUID.randomUUID(),
            -1,
            "",
            Address("", -1),
            false
        ),
        PlayerWebConfig("", false),
        PermissionPlayerConfiguration(
            UUID.randomUUID(),
            emptyList()
        )
    )

    fun toConfiguration(): OfflineCloudPlayerConfiguration {
        return OfflineCloudPlayerConfiguration(
            this.name,
            this.uniqueId,
            this.firstLogin,
            this.lastLogin,
            this.onlineTime,
            this.displayName,
            this.lastPlayerConnection,
            this.webConfig,
            this.permissionConfiguration
        )
    }

    companion object {
        fun fromConfiguration(configuration: OfflineCloudPlayerConfiguration): CloudPlayerEntity {
            return CloudPlayerEntity(
                configuration.uniqueId,
                configuration.name,
                configuration.firstLogin,
                configuration.lastLogin,
                configuration.onlineTime,
                configuration.displayName,
                configuration.lastPlayerConnection,
                configuration.webConfig,
                configuration.permissionPlayerConfiguration
            )
        }
    }

}