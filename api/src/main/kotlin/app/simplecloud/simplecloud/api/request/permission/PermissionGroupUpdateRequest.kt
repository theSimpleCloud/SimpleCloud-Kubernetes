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
import app.simplecloud.simplecloud.api.permission.PermissionGroup

/**
 * Date: 20.03.22
 * Time: 09:11
 * @author Frederick Baier
 *
 */
interface PermissionGroupUpdateRequest : PermissionEntityUpdateRequest {

    override fun getEntity(): PermissionGroup

    override fun clearPermissions(): PermissionGroupUpdateRequest

    override fun addPermission(permission: Permission): PermissionGroupUpdateRequest

    override fun removePermission(permissionString: String): PermissionGroupUpdateRequest

    override fun clearPermissionGroups(): PermissionGroupUpdateRequest

    override fun addPermissionGroup(permissionGroup: PermissionGroup, expiresAt: Long): PermissionGroupUpdateRequest

    override fun removePermissionGroup(groupName: String): PermissionGroupUpdateRequest

    /**
     * Sets the priority for the group (higher is better)
     */
    fun setPriority(priority: Int): PermissionGroupUpdateRequest

}