package app.simplecloud.simplecloud.restserver.base.user

/**
 * Date: 14.03.22
 * Time: 12:20
 * @author Frederick Baier
 *
 */
object EmptyRequestEntity : RequestEntity {
    override fun hasPermission(permission: String): Boolean {
        return false
    }
}