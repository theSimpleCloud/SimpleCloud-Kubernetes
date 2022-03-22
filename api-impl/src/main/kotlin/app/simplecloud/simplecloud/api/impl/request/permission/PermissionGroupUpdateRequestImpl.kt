package app.simplecloud.simplecloud.api.impl.request.permission

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

    override fun submit(): CompletableFuture<Unit> {
        val configuration = PermissionGroupConfiguration(
            this.group.getName(),
            this.priority,
            this.permissions.map { it.toConfiguration() }
        )
        return this.internalService.updateGroupInternal(configuration)
    }

}