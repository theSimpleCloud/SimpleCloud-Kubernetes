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

import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionEntity
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.configuration.PermissionConfiguration
import app.simplecloud.simplecloud.api.request.permission.PermissionEntityUpdateRequest
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Date: 21.03.22
 * Time: 10:43
 * @author Frederick Baier
 *
 */
abstract class AbstractPermissionEntityUpdateRequest(
    private val entity: PermissionEntity,
    private val permissionFactory: Permission.Factory
) : PermissionEntityUpdateRequest {

    protected val permissions = CopyOnWriteArrayList(this.entity.getPermissions())

    override fun getEntity(): PermissionEntity {
        return this.entity
    }

    override fun clearPermissions(): PermissionEntityUpdateRequest {
        this.permissions.clear()
        return this
    }

    override fun addPermission(permission: Permission): PermissionEntityUpdateRequest {
        removePermission(permission.getRawString())
        this.permissions.add(permission)
        return this
    }

    override fun removePermission(permissionString: String): PermissionEntityUpdateRequest {
        this.permissions.removeIf { it.getRawString() == permissionString.lowercase() }
        return this
    }

    override fun clearPermissionGroups(): PermissionEntityUpdateRequest {
        this.permissions.removeIf { it.getRawString().startsWith("group.") }
        return this
    }

    override fun addPermissionGroup(permissionGroup: PermissionGroup, expiresAt: Long): PermissionEntityUpdateRequest {
        val newPermission = PermissionConfiguration(
            "group.${permissionGroup.getName()}",
            true,
            expiresAt,
            null
        )
        addPermission(this.permissionFactory.create(newPermission))
        return this
    }

    override fun removePermissionGroup(groupName: String): PermissionEntityUpdateRequest {
        removePermission("group.${groupName}")
        return this
    }
}