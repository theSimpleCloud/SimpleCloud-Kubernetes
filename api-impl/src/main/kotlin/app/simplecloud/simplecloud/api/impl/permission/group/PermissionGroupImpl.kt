package app.simplecloud.simplecloud.api.impl.permission.group

import app.simplecloud.simplecloud.api.impl.permission.entity.PermissionEntityImpl
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.service.PermissionGroupService
import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted

/**
 * Date: 19.03.22
 * Time: 19:55
 * @author Frederick Baier
 *
 */
class PermissionGroupImpl @Inject constructor(
    @Assisted private val configuration: PermissionGroupConfiguration,
    factory: Permission.Factory,
    permissionGroupService: PermissionGroupService
) : PermissionEntityImpl(
    configuration.permissions.map { factory.create(it) },
    permissionGroupService
), PermissionGroup {

    override fun getName(): String {
        return this.configuration.name
    }

    override fun getPriority(): Int {
        return this.configuration.priority
    }

    override fun toConfiguration(): PermissionGroupConfiguration {
        return this.configuration
    }

}