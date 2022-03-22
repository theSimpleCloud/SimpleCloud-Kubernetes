package app.simplecloud.simplecloud.api.impl.request.permission

import app.simplecloud.simplecloud.api.future.isCompletedNormally
import app.simplecloud.simplecloud.api.internal.service.InternalPermissionGroupService
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.request.permission.PermissionGroupCreateRequest
import com.ea.async.Async.await
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

    override fun submit(): CompletableFuture<PermissionGroup> {
        if (await(doesGroupExist(configuration.name))) {
            throw IllegalArgumentException("Group already exists")
        }
        return this.internalService.createGroupInternal(configuration)
    }

    private fun doesGroupExist(groupName: String): CompletableFuture<Boolean> {
        val completableFuture = this.internalService.findPermissionGroupByName(groupName)
        return completableFuture.handle { _, _ -> completableFuture.isCompletedNormally }
    }
}