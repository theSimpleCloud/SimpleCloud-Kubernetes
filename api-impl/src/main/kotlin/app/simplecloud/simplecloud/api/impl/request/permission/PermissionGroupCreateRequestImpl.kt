package app.simplecloud.simplecloud.api.impl.request.permission

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.api.internal.service.InternalPermissionGroupService
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.request.permission.PermissionGroupCreateRequest
import java.util.concurrent.CompletableFuture

/**
 * Date: 20.03.22
 * Time: 22:21
 * @author Frederick Baier
 *
 */
class PermissionGroupCreateRequestImpl(
    private val configuration: PermissionGroupConfiguration,
    private val internalService: InternalPermissionGroupService
) : PermissionGroupCreateRequest {

    override fun submit(): CompletableFuture<PermissionGroup> = CloudScope.future {
        if (doesGroupExist(configuration.name)) {
            throw IllegalArgumentException("Group already exists")
        }
        return@future internalService.createGroupInternal(configuration)
    }

    private suspend fun doesGroupExist(groupName: String): Boolean {
        return try {
            this.internalService.findPermissionGroupByName(groupName).await()
            true
        } catch (e: NoSuchElementException) {
            false
        }
    }
}