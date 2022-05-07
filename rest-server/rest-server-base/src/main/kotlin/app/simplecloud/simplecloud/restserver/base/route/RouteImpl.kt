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

package app.simplecloud.simplecloud.restserver.base.route

import app.simplecloud.simplecloud.restserver.api.ParameterType
import app.simplecloud.simplecloud.restserver.api.Request
import app.simplecloud.simplecloud.restserver.api.route.RequestType
import app.simplecloud.simplecloud.restserver.api.route.Route
import app.simplecloud.simplecloud.restserver.api.vmethod.VirtualMethod

/**
 * Date: 14.03.22
 * Time: 11:22
 * @author Frederick Baier
 *
 */
class RouteImpl(
    private val requestType: RequestType,
    private val path: String,
    private val permission: String,
    private val parameters: List<ParameterType>,
    private val virtualMethod: VirtualMethod,
): Route {

    override fun getPath(): String {
        return this.path
    }

    override fun getPermission(): String {
        return this.permission
    }

    override fun getRequestType(): RequestType {
        return this.requestType
    }

    override fun handleRequest(request: Request): Any? {
        val methodValues = this.parameters.map { it.resolveValue(request) }
        return this.virtualMethod.invoke(*methodValues.toTypedArray())
    }



}