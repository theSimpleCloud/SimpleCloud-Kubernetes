package app.simplecloud.simplecloud.api.impl.request.permission

import app.simplecloud.simplecloud.api.future.toFutureList
import app.simplecloud.simplecloud.api.future.unitFuture
import app.simplecloud.simplecloud.api.internal.service.InternalPermissionGroupService
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.request.permission.PermissionGroupUpdateRequest
import com.ea.async.Async.await
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
        await(checkForRecursion())
        val configuration = PermissionGroupConfiguration(
            this.group.getName(),
            this.priority,
            this.permissions.map { it.toConfiguration() }
        )
        await(this.internalService.updateGroupInternal(configuration))
        return unitFuture()
    }

    private fun checkForRecursion(): CompletableFuture<Unit> {
        if (await(doesAnySubGroupHasThisGroupAsDependency()))
            throw GroupRecursionException("Recursion detected within group ${this.group.getName()}")
        return unitFuture()
    }

    private fun doesAnySubGroupHasThisGroupAsDependency(): CompletableFuture<Boolean> {
        val thisGroupPermission = "group.${this.group.getName()}"
        val subGroups = await(getSubGroups())

        val hasPermissionFutureList = subGroups.map { it.hasPermission(thisGroupPermission) }.toFutureList()
        return hasPermissionFutureList.thenApply { it.any() }
    }

    private fun getSubGroupNames(): List<String> {
        return this.permissions.filter { it.getRawString().startsWith("group.") }
            .map { it.getRawString().replaceFirst("group.", "") }
    }

    private fun getSubGroups(): CompletableFuture<List<PermissionGroup>> {
        val subGroupNames = getSubGroupNames()
        return subGroupNames.map { this.internalService.findPermissionGroupByName(it) }.toFutureList()
    }

    class GroupRecursionException(message: String) : Exception(message)

}