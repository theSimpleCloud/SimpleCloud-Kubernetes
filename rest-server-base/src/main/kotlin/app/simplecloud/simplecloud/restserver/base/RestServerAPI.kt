package app.simplecloud.simplecloud.restserver.base

import app.simplecloud.simplecloud.restserver.base.impl.RestServerBase
import app.simplecloud.simplecloud.restserver.base.parameter.ParameterType
import app.simplecloud.simplecloud.restserver.base.route.*
import app.simplecloud.simplecloud.restserver.base.service.AuthService
import app.simplecloud.simplecloud.restserver.base.vmethod.VirtualMethod

/**
 * Date: 14.03.22
 * Time: 13:01
 * @author Frederick Baier
 *
 */
object RestServerAPI {

    fun createRestServer(authService: AuthService, port: Int): RestServer {
        return RestServerBase(authService, port)
    }

    class RouteBuilderImpl : RouteBuilder {

        private var path: String? = null
        private var permission: String = ""
        private var requestType: RequestType = RequestType.GET
        private var routeMethod: RouteMethod? = null

        override fun withPath(path: String): RouteBuilder {
            this.path = path
            return this
        }

        override fun withPermission(permission: String): RouteBuilder {
            this.permission = permission
            return this
        }

        override fun withRequestType(requestType: RequestType): RouteBuilder {
            this.requestType = requestType
            return this
        }

        override fun withMethod(routeMethod: RouteMethod): RouteBuilder {
            this.routeMethod = routeMethod
            return this
        }

        override fun build(): Route {
            check(this.path != null)
            check(this.routeMethod != null)
            return RouteImpl(
                this.requestType,
                this.path!!,
                this.permission,
                this.routeMethod!!.getParameters(),
                this.routeMethod!!.getVirtualMethod()
            )
        }

    }

    class RouteMethodBuilderImpl : RouteMethodBuilder {

        private val parameters = ArrayList<ParameterType>()
        private var virtualMethod: VirtualMethod? = null

        override fun setVirtualMethod(virtualMethod: VirtualMethod): RouteMethodBuilder {
            this.virtualMethod = virtualMethod
            return this
        }

        override fun addParameter(parameterType: ParameterType): RouteMethodBuilder {
            this.parameters.add(parameterType)
            return this
        }

        override fun build(): RouteMethod {
            check(this.virtualMethod != null)
            return RouteMethodImpl(this.virtualMethod!!, this.parameters)
        }

    }


}