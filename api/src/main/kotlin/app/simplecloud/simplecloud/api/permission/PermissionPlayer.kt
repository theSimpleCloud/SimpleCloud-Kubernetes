package app.simplecloud.simplecloud.api.permission

import app.simplecloud.simplecloud.api.permission.configuration.PermissionPlayerConfiguration
import java.util.*

/**
 * Date: 19.03.22
 * Time: 11:28
 * @author Frederick Baier
 *
 */
interface PermissionPlayer : PermissionEntity {

    /**
     * Returns the unique id of the player this permission player belongs to
     */
    fun getUniqueId(): UUID

    interface Factory {

        fun create(configuration: PermissionPlayerConfiguration): PermissionPlayer

    }

}