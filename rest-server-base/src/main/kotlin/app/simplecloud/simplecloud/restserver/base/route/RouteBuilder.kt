package app.simplecloud.simplecloud.restserver.base.route

/**
 * Date: 14.03.22
 * Time: 09:48
 * @author Frederick Baier
 *
 */
interface RouteBuilder {

    fun setPath(path: String): RouteBuilder

    fun setPermission(permission: String): RouteBuilder

    fun setRequestType(requestType: RequestType): RouteBuilder

    fun setMethod(routeMethod: RouteMethod): RouteBuilder

    fun build(): Route

}