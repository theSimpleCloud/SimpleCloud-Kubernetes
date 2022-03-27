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

import java.util.concurrent.CompletableFuture


interface PermissionEntity {

    /**
     * Returns whether the entity has the specified [permission]
     */
    fun hasPermission(permission: String, processGroup: String? = null): CompletableFuture<Boolean>

    fun getPermissionByMatch(permission: String, processGroup: String?): Permission? {
        val notExpiredPermissions = getPermissions()
        return notExpiredPermissions.firstOrNull { it.matches(permission, processGroup) }
    }

    /**
     * Returns the permission object of the specified [permission]
     */
    fun getPermissionByRawString(permission: String): Permission? {
      return getPermissions().firstOrNull { it.getRawString() == permission }
    }

    /**
     * Returns all permissions of this entity
     */
    fun getPermissions(): Collection<Permission>

    /**
     * Returns whether this
     */
    fun hasAllRightsTopLevel(): Boolean = getPermissions().any { it.getRawString() == "*" }

    /**
     * Returns whether this entity has the specified group
     * A top level group is a group that an entity has directly
     * e.g. You have the "Admin" group and the "Admin" group inherits "Builder".
     * Then "Admin" is the top level group. So hasTopLevelGroup would return false for "Builder" and true for "Admin".
     */
    fun hasTopLevelGroup(groupName: String): Boolean

    /**
     * Returns the group the player is currently in
     */
    fun getTopLevelPermissionGroups(): CompletableFuture<List<PermissionGroup>>

    /**
     * Returns the highest permission group of the player
     * @see PermissionGroup.getPriority
     */
    fun getHighestTopLevelPermissionGroup(): CompletableFuture<PermissionGroup>

}