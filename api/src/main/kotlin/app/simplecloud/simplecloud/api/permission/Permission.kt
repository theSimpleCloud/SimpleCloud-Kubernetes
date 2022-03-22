package app.simplecloud.simplecloud.api.permission

import app.simplecloud.simplecloud.api.permission.configuration.PermissionConfiguration

/**
 * Date: 19.03.22
 * Time: 11:33
 * @author Frederick Baier
 *
 */
interface Permission {

    /**
     * Returns the raw permission string (e.g. worldedit.*)
     */
    fun getRawString(): String

    /**
     * Returns whether this permission matches the [permissionString]
     * @param permissionString the permission to test
     * @param processGroup the group context the permission shall be tested in
     */
    fun matches(permissionString: String, processGroup: String? = null): Boolean

    /**
     *  Returns whether the permission is negative or positive
     */
    fun isActive(): Boolean

    /**
     * Returns the timestamp the permission will expire
     */
    fun getExpireTimestamp(): Long

    /**
     * Returns the configuration of this permission
     */
    fun toConfiguration() : PermissionConfiguration

    interface Factory {

        fun create(configuration: PermissionConfiguration): Permission

    }

}