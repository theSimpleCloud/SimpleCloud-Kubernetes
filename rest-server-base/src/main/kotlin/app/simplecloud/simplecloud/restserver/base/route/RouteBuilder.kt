package app.simplecloud.simplecloud.restserver.base.route

/**
 * Date: 14.03.22
 * Time: 09:48
 * @author Frederick Baier
 *
 */
interface RouteBuilder {

    fun withPath(path: String): RouteBuilder

    fun withPermission(permission: String): RouteBuilder

    fun withRequestType(requestType: RequestType): RouteBuilder

    fun withMethod(routeMethod: RouteMethod): RouteBuilder

    fun build(): Route

}