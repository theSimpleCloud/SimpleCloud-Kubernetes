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

package app.simplecloud.simplecloud.api.impl.permission.entity

import app.simplecloud.simplecloud.api.future.*
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionEntity
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.service.PermissionGroupService
import java.util.concurrent.CompletableFuture


open class PermissionEntityImpl(
    private val permissions: List<Permission>,
    private val permissionGroupService: PermissionGroupService
) : PermissionEntity {

    override fun getPermissions(): Collection<Permission> = this.permissions

    override fun hasPermission(permission: String, processGroup: String?): CompletableFuture<Boolean> {
        if (permission.isBlank() || hasAllRightsTopLevel()) return completedFuture(true)
        val permissionObj = getPermissionByMatch(permission, processGroup)
        if (permissionObj != null) return completedFuture(permissionObj.isActive())
        return hasPermissionInGroups(permission, processGroup)
    }

    private fun hasPermissionInGroups(
        permission: String,
        processGroup: String?
    ): CompletableFuture<Boolean> = CloudScope.future {
        val topLevelGroups = getTopLevelPermissionGroups().await()
        return@future topLevelGroups.any { it.hasPermission(permission, processGroup).await() }
    }

    override fun hasTopLevelGroup(groupName: String): Boolean {
        return getPermissions().any { it.getRawString() == "group.${groupName}".lowercase() && it.isActive() }
    }

    override fun getTopLevelPermissionGroups(): CompletableFuture<List<PermissionGroup>> {
        val permissionGroupNames = getPermissionGroupNames()
        return permissionGroupNames.map { this.permissionGroupService.findByName(it) }.toFutureList()
    }

    override fun getHighestTopLevelPermissionGroup(): CompletableFuture<PermissionGroup> {
        return getTopLevelPermissionGroups().thenApply { list -> list.maxByOrNull { it.getPriority() } }.nonNull()
    }

    private fun getPermissionGroupNames(): List<String> {
        return getPermissions()
            .filter { it.getRawString().startsWith("group.") && it.isActive() }
            .map { it.getRawString().replaceFirst("group.", "") }
    }

}