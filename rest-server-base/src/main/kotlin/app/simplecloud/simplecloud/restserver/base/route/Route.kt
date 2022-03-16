package app.simplecloud.simplecloud.restserver.base.route

import app.simplecloud.simplecloud.restserver.base.request.Request

/**
 * Date: 14.03.22
 * Time: 11:15
 * @author Frederick Baier
 *
 */
interface Route {

    fun getRequestType(): RequestType

    fun getPath(): String

    /**
     * Returns the permission or an empty string if no permission was set
     */
    fun getPermission(): String

    fun hasPermission(): Boolean {
        return getPermission().isNotEmpty()
    }

    fun handleRequest(request: Request): Any?

}