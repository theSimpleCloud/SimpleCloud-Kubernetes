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

package app.simplecloud.simplecloud.api.impl.request.permission

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.api.future.toFutureList
import app.simplecloud.simplecloud.api.internal.service.InternalPermissionGroupService
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.request.permission.PermissionGroupUpdateRequest
import java.util.concurrent.CompletableFuture

/**
 * Date: 20.03.22
 * Time: 22:21
 * @author Frederick Baier
 *
 */
class PermissionGroupUpdateRequestImpl(
    private val group: PermissionGroup,
    private val internalService: InternalPermissionGroupService,
    private val permissionFactory: Permission.Factory
) : AbstractPermissionEntityUpdateRequest(group, permissionFactory), PermissionGroupUpdateRequest {

    @Volatile
    private var priority = this.group.getPriority()

    override fun getEntity(): PermissionGroup {
        return this.group
    }

    override fun clearPermissions(): PermissionGroupUpdateRequest {
        super.clearPermissions()
        return this
    }

    override fun addPermission(permission: Permission): PermissionGroupUpdateRequest {
        super.addPermission(permission)
        return this
    }

    override fun removePermission(permissionString: String): PermissionGroupUpdateRequest {
        super.removePermission(permissionString)
        return this
    }

    override fun clearPermissionGroups(): PermissionGroupUpdateRequest {
        super.clearPermissionGroups()
        return this
    }

    override fun addPermissionGroup(permissionGroup: PermissionGroup, expiresAt: Long): PermissionGroupUpdateRequest {
        super.addPermissionGroup(permissionGroup, expiresAt)
        return this
    }

    override fun removePermissionGroup(groupName: String): PermissionGroupUpdateRequest {
        super.removePermissionGroup(groupName)
        return this
    }

    override fun setPriority(priority: Int): PermissionGroupUpdateRequest {
        this.priority = priority
        return this
    }

    override fun submit(): CompletableFuture<Unit> = CloudScope.future {
        checkForRecursion()
        val configuration = PermissionGroupConfiguration(
            group.getName(),
            priority,
            permissions.map { it.toConfiguration() }
        )
        internalService.updateGroupInternal(configuration)
    }

    private suspend fun checkForRecursion() {
        if (doesAnySubGroupHasThisGroupAsDependency())
            throw GroupRecursionException("Recursion detected within group ${this.group.getName()}")
    }

    private suspend fun doesAnySubGroupHasThisGroupAsDependency(): Boolean {
        val thisGroupPermission = "group.${this.group.getName()}"
        val subGroups = getSubGroups()

        return subGroups.any { it.hasPermission(thisGroupPermission).await() }
    }

    private fun getSubGroupNames(): List<String> {
        return this.permissions.filter { it.getRawString().startsWith("group.") }
            .map { it.getRawString().replaceFirst("group.", "") }
    }

    private suspend fun getSubGroups(): List<PermissionGroup> {
        val subGroupNames = getSubGroupNames()
        return subGroupNames.map { this.internalService.findByName(it) }.toFutureList().await()
    }

    class GroupRecursionException(message: String) : Exception(message)

}