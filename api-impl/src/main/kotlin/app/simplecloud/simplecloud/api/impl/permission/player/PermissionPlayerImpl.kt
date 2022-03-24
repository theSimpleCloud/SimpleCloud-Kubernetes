package app.simplecloud.simplecloud.api.impl.permission.player

import app.simplecloud.simplecloud.api.impl.permission.entity.PermissionEntityImpl
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionPlayer
import app.simplecloud.simplecloud.api.permission.configuration.PermissionPlayerConfiguration
import app.simplecloud.simplecloud.api.service.PermissionGroupService
import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import java.util.*

/**
 * Date: 19.03.22
 * Time: 16:34
 * @author Frederick Baier
 *
 */
class PermissionPlayerImpl @Inject constructor(
    @Assisted private val configuration: PermissionPlayerConfiguration,
    private val permissionGroupService: PermissionGroupService,
    factory: Permission.Factory
) : PermissionEntityImpl(
    configuration.permissions.map { factory.create(it) },
    permissionGroupService
), PermissionPlayer {

    override fun getUniqueId(): UUID {
        return this.configuration.uniqueId
    }

}