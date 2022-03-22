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

package app.simplecloud.simplecloud.api.impl.permission.entity

import app.simplecloud.simplecloud.api.future.completedFuture
import app.simplecloud.simplecloud.api.future.nonNull
import app.simplecloud.simplecloud.api.future.toFutureList
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionEntity
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.service.PermissionGroupService
import com.ea.async.Async.await
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

    private fun hasPermissionInGroups(permission: String, processGroup: String?): CompletableFuture<Boolean> {
        val topLevelGroups = await(getTopLevelPermissionGroups())
        val hasPermissionsFuture = topLevelGroups.map { it.hasPermission(permission, processGroup) }.toFutureList()
        return hasPermissionsFuture.thenApply { it.any() }
    }

    override fun hasTopLevelGroup(groupName: String): Boolean {
        return getPermissions().any { it.getRawString() == "group.${groupName}" && it.isActive() }
    }

    override fun getTopLevelPermissionGroups(): CompletableFuture<List<PermissionGroup>> {
        val permissionGroupNames = getPermissionGroupNames()
        return permissionGroupNames.map { this.permissionGroupService.findPermissionGroupByName(it) }.toFutureList()
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