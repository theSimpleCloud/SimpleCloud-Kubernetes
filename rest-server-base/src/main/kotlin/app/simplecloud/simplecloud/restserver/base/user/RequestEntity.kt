package app.simplecloud.simplecloud.restserver.base.user

/**
 * Date: 14.03.22
 * Time: 10:05
 * @author Frederick Baier
 *
 */
interface RequestEntity {

    fun hasPermission(permission: String): Boolean

}