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

import app.simplecloud.simplecloud.restserver.base.parameter.PathParamParameterType
import app.simplecloud.simplecloud.restserver.base.parameter.RequestBodyParameterType
import app.simplecloud.simplecloud.restserver.base.parameter.RequestingEntityParameterType
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
        .addParameter(RequestingEntityParameterType())
        .setVirtualMethod(object : VirtualMethod {
            override fun invoke(vararg args: Any?): Any? {
                return args
            }
        }).build()
    val route = RestServerAPI.RouteBuilderImpl()
        .setRequestType(RequestType.POST)
        .setPath("test/{name}")
        .setMethod(routeMethod)
        .build()
    restServer.registerRoute(route)
}