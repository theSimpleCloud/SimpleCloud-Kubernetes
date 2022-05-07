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

package app.simplecloud.simplecloud.restserver.base.request

import app.simplecloud.simplecloud.restserver.api.route.*
import app.simplecloud.simplecloud.restserver.base.route.RouteImpl

/**
 * Date: 03.05.22
 * Time: 18:44
 * @author Frederick Baier
 *
 */
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

    override fun newRouteMethodBuilder(): RouteMethodBuilder {
        return RouteMethodBuilderImpl()
    }

}