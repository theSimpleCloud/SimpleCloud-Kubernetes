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

package app.simplecloud.simplecloud.api.request.permission

import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionEntity
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.utils.Request

/**
 * Date: 20.03.22
 * Time: 09:06
 * @author Frederick Baier
 *
 */
interface PermissionEntityUpdateRequest : Request<Unit> {

    /**
     * Returns the entity to update
     */
    fun getEntity(): PermissionEntity

    /**
     *  Clears all permission from the entity
     */
    fun clearPermissions(): PermissionEntityUpdateRequest

    /**
     * Adds a permission to the entity
     */
    fun addPermission(permission: Permission): PermissionEntityUpdateRequest

    /**
     * Removes the permission found by the specified [permissionString]
     */
    fun removePermission(permissionString: String): PermissionEntityUpdateRequest

    /**
     * Clears all permission groups from the entity
     */
    fun clearPermissionGroups(): PermissionEntityUpdateRequest

    /**
     * Adds the group to the entity
     * @param permissionGroup the group to be added
     * @param expiresAt the timestamp of expiry (-1 for no expiry)
     */
    fun addPermissionGroup(permissionGroup: PermissionGroup, expiresAt: Long): PermissionEntityUpdateRequest

    /**
     * Removes the group found by name from the entity
     */
    fun removePermissionGroup(groupName: String): PermissionEntityUpdateRequest


}