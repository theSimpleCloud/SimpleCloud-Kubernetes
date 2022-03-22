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
            ""
        )
        addPermission(this.permissionFactory.create(newPermission))
        return this
    }

    override fun removePermissionGroup(groupName: String): PermissionEntityUpdateRequest {
        removePermission("group.${groupName}")
        return this
    }
}