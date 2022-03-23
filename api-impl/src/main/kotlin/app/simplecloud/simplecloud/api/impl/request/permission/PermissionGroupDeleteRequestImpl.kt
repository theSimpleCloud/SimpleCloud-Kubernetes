package app.simplecloud.simplecloud.api.impl.request.permission

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.api.internal.service.InternalPermissionGroupService
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.request.permission.PermissionGroupDeleteRequest
import java.util.concurrent.CompletableFuture

/**
 * Date: 20.03.22
 * Time: 22:21
 * @author Frederick Baier
 *
 */
class PermissionGroupDeleteRequestImpl(
    private val permissionGroup: PermissionGroup,
    private val internalService: InternalPermissionGroupService
) : PermissionGroupDeleteRequest {

    override fun getPermissionGroup(): PermissionGroup {
        return this.permissionGroup
    }

    override fun submit(): CompletableFuture<Unit> = CloudScope.future {
        internalService.deleteGroupInternal(permissionGroup)
    }
}