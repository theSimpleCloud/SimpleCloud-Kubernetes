/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
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