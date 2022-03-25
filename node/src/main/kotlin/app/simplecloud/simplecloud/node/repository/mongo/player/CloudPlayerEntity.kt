/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package app.simplecloud.simplecloud.node.repository.mongo.player

import app.simplecloud.simplecloud.api.permission.configuration.PermissionPlayerConfiguration
import app.simplecloud.simplecloud.api.player.PlayerWebConfig
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.api.utils.Address
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