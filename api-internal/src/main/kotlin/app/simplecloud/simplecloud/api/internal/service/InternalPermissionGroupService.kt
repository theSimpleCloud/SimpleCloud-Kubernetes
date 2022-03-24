package app.simplecloud.simplecloud.api.internal.service

import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.service.PermissionGroupService

/**
 * Date: 20.03.22
 * Time: 18:54
 * @author Frederick Baier
 *
 */
interface InternalPermissionGroupService : PermissionGroupService {

    suspend fun updateGroupInternal(configuration: PermissionGroupConfiguration)

    suspend fun deleteGroupInternal(group: PermissionGroup)

    suspend fun createGroupInternal(configuration: PermissionGroupConfiguration): PermissionGroup

}