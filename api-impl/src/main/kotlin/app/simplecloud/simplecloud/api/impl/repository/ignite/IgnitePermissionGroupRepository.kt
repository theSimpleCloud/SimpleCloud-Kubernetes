package app.simplecloud.simplecloud.api.impl.repository.ignite

import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.permission.repository.PermissionGroupRepository
import com.google.inject.Inject
import com.google.inject.Singleton
import org.apache.ignite.Ignite

/**
 * Date: 20.03.22
 * Time: 13:09
 * @author Frederick Baier
 *
 */
@Singleton
class IgnitePermissionGroupRepository @Inject constructor(
    private val ignite: Ignite
) : AbstractIgniteRepository<String, PermissionGroupConfiguration>(
    ignite.getOrCreateCache("cloud-permission-groups")
), PermissionGroupRepository {
}