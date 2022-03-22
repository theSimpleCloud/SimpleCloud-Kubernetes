package app.simplecloud.simplecloud.api.permission

import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration

/**
 * Date: 19.03.22
 * Time: 12:00
 * @author Frederick Baier
 *
 */
interface PermissionGroup : PermissionEntity {

    /**
     * Returns the name of the group
     */
    fun getName(): String

    /**
     * Returns the priority of this group (higher is better)
     */
    fun getPriority(): Int

    /**
     * Return the configuration of this permission group
     */
    fun toConfiguration(): PermissionGroupConfiguration

    interface Factory {

        fun create(configuration: PermissionGroupConfiguration): PermissionGroup

    }

}