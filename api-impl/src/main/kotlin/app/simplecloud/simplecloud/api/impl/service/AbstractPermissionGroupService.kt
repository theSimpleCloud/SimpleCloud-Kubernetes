package app.simplecloud.simplecloud.api.impl.service

import app.simplecloud.simplecloud.api.impl.request.permission.PermissionGroupCreateRequestImpl
import app.simplecloud.simplecloud.api.impl.request.permission.PermissionGroupDeleteRequestImpl
import app.simplecloud.simplecloud.api.impl.request.permission.PermissionGroupUpdateRequestImpl
import app.simplecloud.simplecloud.api.internal.service.InternalPermissionGroupService
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.permission.repository.PermissionGroupRepository
import app.simplecloud.simplecloud.api.request.permission.PermissionGroupCreateRequest
import app.simplecloud.simplecloud.api.request.permission.PermissionGroupDeleteRequest
import app.simplecloud.simplecloud.api.request.permission.PermissionGroupUpdateRequest
import java.util.concurrent.CompletableFuture

/**
 * Date: 20.03.22
 * Time: 18:56
 * @author Frederick Baier
 *
 */
abstract class AbstractPermissionGroupService(
    private val repository: PermissionGroupRepository,
    private val groupFactory: PermissionGroup.Factory,
    private val permissionFactory: Permission.Factory
) : InternalPermissionGroupService {

    override fun findPermissionGroupByName(name: String): CompletableFuture<PermissionGroup> {
        val completableFuture = this.repository.find(name)
        return completableFuture.thenApply { this.groupFactory.create(it) }
    }

    override fun findAll(): CompletableFuture<List<PermissionGroup>> {
        val completableFuture = this.repository.findAll()
        return completableFuture.thenApply { list -> list.map { this.groupFactory.create(it) } }
    }

    override fun createCreateRequest(configuration: PermissionGroupConfiguration): PermissionGroupCreateRequest {
        return PermissionGroupCreateRequestImpl(configuration, this)
    }

    override fun createDeleteRequest(group: PermissionGroup): PermissionGroupDeleteRequest {
        return PermissionGroupDeleteRequestImpl(group, this)
    }

    override fun createUpdateRequest(group: PermissionGroup): PermissionGroupUpdateRequest {
        return PermissionGroupUpdateRequestImpl(group, this, this.permissionFactory)
    }

    override suspend fun createGroupInternal(configuration: PermissionGroupConfiguration): PermissionGroup {
        val permissionGroup = this.groupFactory.create(configuration)
        updateGroupInternal(configuration)
        return permissionGroup
    }


}