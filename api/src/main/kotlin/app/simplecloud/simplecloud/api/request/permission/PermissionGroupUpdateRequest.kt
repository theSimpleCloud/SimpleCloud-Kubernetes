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