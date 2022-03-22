package app.simplecloud.simplecloud.api.permission

import app.simplecloud.simplecloud.api.future.completedFuture
import app.simplecloud.simplecloud.api.future.failedFuture
import java.util.concurrent.CompletableFuture

/**
 * Date: 17.03.22
 * Time: 21:11
 * @author Frederick Baier
 *
 */
object EmptyPermissionEntity : PermissionEntity {
    override fun hasPermission(permission: String, processGroup: String?): CompletableFuture<Boolean> {
        return completedFuture(false)
    }

    override fun getPermissions(): Collection<Permission> {
        return emptyList()
    }

    override fun hasTopLevelGroup(groupName: String): Boolean {
        return false
    }

    override fun getTopLevelPermissionGroups(): CompletableFuture<List<PermissionGroup>> {
        return completedFuture(emptyList())
    }

    override fun getHighestTopLevelPermissionGroup(): CompletableFuture<PermissionGroup> {
        return failedFuture(NoSuchElementException())
    }

}