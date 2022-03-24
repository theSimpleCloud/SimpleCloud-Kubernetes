package app.simplecloud.simplecloud.api.impl.permission

import app.simplecloud.simplecloud.api.impl.util.TestHashMapRepository
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.repository.PermissionGroupRepository
import com.google.inject.Singleton

/**
 * Date: 23.03.22
 * Time: 09:12
 * @author Frederick Baier
 *
 */
@Singleton
class PermissionGroupRepositoryImpl : TestHashMapRepository<String, PermissionGroupConfiguration>(),
    PermissionGroupRepository