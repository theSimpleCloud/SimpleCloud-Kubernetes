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

package app.simplecloud.simplecloud.restserver.defaultcontroller.v1.dto

import app.simplecloud.simplecloud.api.permission.configuration.PermissionPlayerConfiguration
import app.simplecloud.simplecloud.api.player.PlayerWebConfig
import java.util.*

/**
 * Date: 22.03.22
 * Time: 12:25
 * @author Frederick Baier
 *
 */
class PlayerUpdateRequestDto(
    val uniqueId: UUID,
    val displayName: String,
    val webConfig: PlayerWebConfig,
    val permissionPlayerConfiguration: PermissionPlayerConfiguration
) {

    private constructor() : this(
        UUID.randomUUID(),
        "",
        PlayerWebConfig("", false),
        PermissionPlayerConfiguration(UUID.randomUUID(), emptyList())
    )

}