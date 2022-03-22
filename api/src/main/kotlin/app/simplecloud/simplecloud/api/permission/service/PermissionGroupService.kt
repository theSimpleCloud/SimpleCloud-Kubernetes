package app.simplecloud.simplecloud.api.permission.service

import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.request.permission.PermissionGroupCreateRequest
import app.simplecloud.simplecloud.api.request.permission.PermissionGroupDeleteRequest
import app.simplecloud.simplecloud.api.request.permission.PermissionGroupUpdateRequest
import app.simplecloud.simplecloud.api.service.Service
import java.util.concurrent.CompletableFuture

/**
 * Date: 20.03.22
 * Time: 12:37
 * @author Frederick Baier
 *
 */
interface PermissionGroupService : Service {

    fun findAll(): CompletableFuture<List<PermissionGroup>>

    fun findPermissionGroupByName(name: String): CompletableFuture<PermissionGroup>

    fun createUpdateRequest(group: PermissionGroup): PermissionGroupUpdateRequest

    fun createCreateRequest(configuration: PermissionGroupConfiguration): PermissionGroupCreateRequest

    fun createDeleteRequest(group: PermissionGroup): PermissionGroupDeleteRequest

}