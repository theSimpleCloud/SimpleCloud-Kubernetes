package app.simplecloud.simplecloud.restserver.base

import app.simplecloud.simplecloud.restserver.base.parameter.PathParamParameterType
import app.simplecloud.simplecloud.restserver.base.parameter.RequestBodyParameterType
import app.simplecloud.simplecloud.restserver.base.parameter.RequestingUserParameterType
import app.simplecloud.simplecloud.restserver.base.route.RequestType
import app.simplecloud.simplecloud.restserver.base.service.NoAuthService
import app.simplecloud.simplecloud.restserver.base.service.UsernameAndPasswordCredentials
import app.simplecloud.simplecloud.restserver.base.vmethod.VirtualMethod

/**
 * Date: 14.03.22
 * Time: 13:04
 * @author Frederick Baier
 *
 */
fun main() {
    val restServer = RestServerAPI.createRestServer(NoAuthService(), 8008)
    val routeMethod = RestServerAPI.RouteMethodBuilderImpl()
        .addParameter(PathParamParameterType("name"))
        .addParameter(RequestBodyParameterType.singleClass(UsernameAndPasswordCredentials::class.java))
        .addParameter(RequestingUserParameterType())
        .setVirtualMethod(object : VirtualMethod {
            override fun invoke(vararg args: Any?): Any? {
                return args
            }
        }).build()
    val route = RestServerAPI.RouteBuilderImpl()
        .withRequestType(RequestType.POST)
        .withPath("test/{name}")
        .withMethod(routeMethod)
        .build()
    restServer.registerRoute(route)
}