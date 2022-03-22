package app.simplecloud.simplecloud.api.permission.configuration

/**
 * Date: 19.03.22
 * Time: 20:01
 * @author Frederick Baier
 *
 */
class PermissionGroupConfiguration(
    val name: String,
    val priority: Int,
    val permissions: List<PermissionConfiguration>,
) {

    private constructor() : this("", -1, emptyList())

}