package app.simplecloud.simplecloud.api.request.permission

import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.PermissionPlayer

/**
 * Date: 20.03.22
 * Time: 09:15
 * @author Frederick Baier
 *
 */
interface PermissionPlayerUpdateRequest : PermissionEntityUpdateRequest {

    override fun getEntity(): PermissionPlayer

    override fun clearPermissions(): PermissionPlayerUpdateRequest

    override fun addPermission(permission: Permission): PermissionPlayerUpdateRequest

    override fun removePermission(permissionString: String): PermissionPlayerUpdateRequest

    override fun clearPermissionGroups(): PermissionPlayerUpdateRequest

    override fun addPermissionGroup(permissionGroup: PermissionGroup, expiresAt: Long): PermissionPlayerUpdateRequest

    override fun removePermissionGroup(groupName: String): PermissionPlayerUpdateRequest

}