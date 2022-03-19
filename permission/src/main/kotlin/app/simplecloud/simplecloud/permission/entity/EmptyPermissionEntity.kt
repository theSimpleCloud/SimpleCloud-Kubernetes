package app.simplecloud.simplecloud.permission.entity

import app.simplecloud.simplecloud.permission.Permission

/**
 * Date: 17.03.22
 * Time: 21:11
 * @author Frederick Baier
 *
 */
object EmptyPermissionEntity : PermissionEntity {

    override fun getPermissions(): Collection<Permission> {
        return emptyList()
    }

    override fun addPermission(permission: Permission) {
        throw UnsupportedOperationException()
    }

    override fun removePermission(permissionString: String) {
        throw UnsupportedOperationException()
    }

    override fun clearAllPermission() {
    }
}