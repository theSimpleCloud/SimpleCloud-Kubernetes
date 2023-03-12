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

package app.simplecloud.simplecloud.node.resource.player

import app.simplecloud.simplecloud.api.permission.configuration.PermissionPlayerConfiguration
import app.simplecloud.simplecloud.api.player.PlayerWebConfig
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.node.resource.permissiongroup.V1Beta1PermissionConfiguration
import java.util.*

/**
 * Date: 10.03.23
 * Time: 17:41
 * @author Frederick Baier
 *
 */
class V1Beta1CloudPlayerSpec(
    val name: String,
    val firstLogin: Long,
    val lastLogin: Long,
    val onlineTime: Long,
    val displayName: String,
    val lastPlayerConnection: V1Beta1PlayerConnectionConfiguration,
    val webConfig: V1Beta1PlayerWebConfig,
    val permissionPlayerConfiguration: V1Beta1PermissionPlayerConfiguration,
) {

    fun toOfflineCloudPlayerConfig(playerUniqueId: UUID): OfflineCloudPlayerConfiguration {
        return OfflineCloudPlayerConfiguration(
            name,
            playerUniqueId,
            firstLogin,
            lastLogin,
            onlineTime,
            displayName,
            PlayerConnectionConfiguration(
                lastPlayerConnection.uniqueId,
                lastPlayerConnection.numericalClientVersion,
                lastPlayerConnection.name,
                Address(lastPlayerConnection.addressHost, lastPlayerConnection.addressPort),
                lastPlayerConnection.onlineMode
            ),
            PlayerWebConfig(webConfig.password, webConfig.hasAccess),
            PermissionPlayerConfiguration(
                permissionPlayerConfiguration.uniqueId,
                permissionPlayerConfiguration.permissions.map { it.toConfiguration() })
        )
    }

    companion object {
        fun fromConfig(configuration: OfflineCloudPlayerConfiguration): V1Beta1CloudPlayerSpec {
            return V1Beta1CloudPlayerSpec(
                configuration.name,
                configuration.firstLogin,
                configuration.lastLogin,
                configuration.onlineTime,
                configuration.displayName,
                V1Beta1PlayerConnectionConfiguration(
                    configuration.lastPlayerConnection.uniqueId,
                    configuration.lastPlayerConnection.numericalClientVersion,
                    configuration.lastPlayerConnection.name,
                    configuration.lastPlayerConnection.address.host,
                    configuration.lastPlayerConnection.address.port,
                    configuration.lastPlayerConnection.onlineMode
                ),
                V1Beta1PlayerWebConfig(configuration.webConfig.password, configuration.webConfig.hasAccess),
                V1Beta1PermissionPlayerConfiguration(
                    configuration.permissionPlayerConfiguration.uniqueId,
                    configuration.permissionPlayerConfiguration.permissions.map {
                        V1Beta1PermissionConfiguration(
                            it.permissionString,
                            it.active,
                            it.expiresAtTimestamp,
                            it.targetProcessGroup
                        )
                    }.toTypedArray()
                )
            )
        }
    }

}