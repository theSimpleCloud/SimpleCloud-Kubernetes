/*
 * SimpleCloud is a software for administrating a minecraft server network.
 * Copyright (C) 2022 Frederick Baier & Philipp Eistrach
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

        override fun setPath(path: String): RouteBuilder {
            this.path = path
            return this
        }

        override fun setPermission(permission: String): RouteBuilder {
            this.permission = permission
            return this
        }

        override fun setRequestType(requestType: RequestType): RouteBuilder {
            this.requestType = requestType
            return this
        }

        override fun setMethod(routeMethod: RouteMethod): RouteBuilder {
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