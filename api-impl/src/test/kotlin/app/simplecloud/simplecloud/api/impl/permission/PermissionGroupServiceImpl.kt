package app.simplecloud.simplecloud.api.impl.permission

import app.simplecloud.simplecloud.api.impl.service.AbstractPermissionGroupService
import app.simplecloud.simplecloud.api.internal.service.InternalPermissionGroupService
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.permission.repository.PermissionGroupRepository
import com.google.inject.Inject
import com.google.inject.Singleton
import java.util.concurrent.CompletableFuture

/**
 * Date: 23.03.22
 * Time: 09:01
 * @author Frederick Baier
 *
 */
@Singleton
class PermissionGroupServiceImpl @Inject constructor(
    private val repository: PermissionGroupRepository,
    private val groupFactory: PermissionGroup.Factory,
    private val permissionFactory: Permission.Factory
) : AbstractPermissionGroupService(repository, groupFactory, permissionFactory), InternalPermissionGroupService {

    override fun updateGroupInternal(configuration: PermissionGroupConfiguration): CompletableFuture<Unit> {
        return this.repository.save(configuration.name, configuration)
    }

    override fun deleteGroupInternal(group: PermissionGroup) {
        TODO("Not yet implemented")
    }
}