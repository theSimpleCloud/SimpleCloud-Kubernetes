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

package app.simplecloud.simplecloud.api.permission.configuration

import java.io.Serializable

/**
 * Date: 19.03.22
 * Time: 20:01
 * @author Frederick Baier
 *
 */
class PermissionGroupConfiguration(
    val name: String,
    val priority: Int,
    val permissions: List<PermissionConfiguration>,
) : Serializable {

    private constructor() : this("", -1, emptyList())

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + priority
        result = 31 * result + permissions.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PermissionGroupConfiguration

        if (name != other.name) return false
        if (priority != other.priority) return false
        if (permissions != other.permissions) return false

        return true
    }

}