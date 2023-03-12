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

/**
 * Date: 19.03.22
 * Time: 16:36
 * @author Frederick Baier
 *
 */
class PermissionConfiguration(
    permissionString: String,
    val active: Boolean,
    val expiresAtTimestamp: Long,
    //empty string means no group set
    val targetProcessGroup: String?,
) : java.io.Serializable {

    val permissionString: String = permissionString.lowercase()

    private constructor() : this("", false, 0L, null)


    override fun hashCode(): Int {
        var result = active.hashCode()
        result = 31 * result + expiresAtTimestamp.hashCode()
        result = 31 * result + (targetProcessGroup?.hashCode() ?: 0)
        result = 31 * result + permissionString.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PermissionConfiguration

        if (active != other.active) return false
        if (expiresAtTimestamp != other.expiresAtTimestamp) return false
        if (targetProcessGroup != other.targetProcessGroup) return false
        if (permissionString != other.permissionString) return false

        return true
    }

}