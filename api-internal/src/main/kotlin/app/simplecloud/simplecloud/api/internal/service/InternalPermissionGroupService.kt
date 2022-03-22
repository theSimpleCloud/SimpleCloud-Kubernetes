package app.simplecloud.simplecloud.api.internal.service

import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.permission.service.PermissionGroupService
import java.util.concurrent.CompletableFuture

/**
 * Date: 20.03.22
 * Time: 18:54
 * @author Frederick Baier
 *
 */
interface InternalPermissionGroupService : PermissionGroupService {

    fun updateGroupInternal(configuration: PermissionGroupConfiguration): CompletableFuture<Unit>

    fun deleteGroupInternal(group: PermissionGroup)

    fun createGroupInternal(configuration: PermissionGroupConfiguration): CompletableFuture<PermissionGroup>

}