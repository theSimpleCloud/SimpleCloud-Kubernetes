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

package app.simplecloud.simplecloud.api.permission

import app.simplecloud.simplecloud.api.permission.configuration.PermissionConfiguration

/**
 * Date: 19.03.22
 * Time: 11:33
 * @author Frederick Baier
 *
 */
interface Permission {

    /**
     * Returns the raw permission string (e.g. worldedit.*)
     */
    fun getRawString(): String

    /**
     * Returns whether this permission matches the [permissionString]
     * @param permissionString the permission to test
     * @param processGroup the group context the permission shall be tested in
     */
    fun matches(permissionString: String, processGroup: String? = null): Boolean

    /**
     *  Returns whether the permission is negative or positive
     */
    fun isActive(): Boolean

    /**
     * Returns the timestamp the permission will expire
     */
    fun getExpireTimestamp(): Long

    /**
     * Returns the configuration of this permission
     */
    fun toConfiguration() : PermissionConfiguration

    interface Factory {

        fun create(configuration: PermissionConfiguration): Permission

    }

}