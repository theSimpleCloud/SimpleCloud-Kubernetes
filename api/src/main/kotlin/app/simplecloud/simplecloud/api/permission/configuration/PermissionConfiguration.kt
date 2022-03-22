package app.simplecloud.simplecloud.api.permission.configuration

/**
 * Date: 19.03.22
 * Time: 16:36
 * @author Frederick Baier
 *
 */
class PermissionConfiguration(
    val permissionString: String,
    val active: Boolean,
    val expiresAtTimestamp: Long,
    //empty string means no group set
    val targetProcessGroup: String?
) {

    constructor() : this("", false, 0L, null)

}