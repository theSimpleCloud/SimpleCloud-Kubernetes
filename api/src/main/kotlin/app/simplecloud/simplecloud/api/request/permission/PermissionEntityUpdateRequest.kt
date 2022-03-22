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