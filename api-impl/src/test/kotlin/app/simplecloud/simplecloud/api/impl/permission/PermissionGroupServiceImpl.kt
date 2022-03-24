package app.simplecloud.simplecloud.api.impl.permission

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.impl.service.AbstractPermissionGroupService
import app.simplecloud.simplecloud.api.internal.service.InternalPermissionGroupService
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.repository.PermissionGroupRepository
import com.google.inject.Inject
import com.google.inject.Singleton

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

    override suspend fun updateGroupInternal(configuration: PermissionGroupConfiguration) {
        this.repository.save(configuration.name, configuration).await()
    }

    override suspend fun deleteGroupInternal(group: PermissionGroup) {
        TODO("Not yet implemented")
    }
}