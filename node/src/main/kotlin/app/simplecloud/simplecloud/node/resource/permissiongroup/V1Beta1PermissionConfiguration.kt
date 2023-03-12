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

package app.simplecloud.simplecloud.node.resource.permissiongroup

import app.simplecloud.simplecloud.api.permission.configuration.PermissionConfiguration
import app.simplecloud.simplecloud.api.resourcedefinition.limitation.annotation.Optional
import app.simplecloud.simplecloud.api.resourcedefinition.limitation.annotation.StringMinLength

/**
 * Date: 09.03.23
 * Time: 14:33
 * @author Frederick Baier
 *
 */
class V1Beta1PermissionConfiguration(
    permissionString: String,
    val active: Boolean,
    val expiresAtTimestamp: Long,
    //empty string means no group set
    @Optional
    val targetProcessGroup: String?,
) {

    @StringMinLength(3)
    val permissionString: String = permissionString.lowercase()

    fun toConfiguration(): PermissionConfiguration {
        return PermissionConfiguration(permissionString, active, expiresAtTimestamp, targetProcessGroup)
    }

}