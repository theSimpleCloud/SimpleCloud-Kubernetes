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

import app.simplecloud.simplecloud.restserver.api.ParameterType
import app.simplecloud.simplecloud.restserver.api.route.RouteMethod
import app.simplecloud.simplecloud.restserver.api.route.RouteMethodBuilder
import app.simplecloud.simplecloud.restserver.api.vmethod.VirtualMethod
import app.simplecloud.simplecloud.restserver.base.parameter.PathParamParameterType
import app.simplecloud.simplecloud.restserver.base.parameter.RequestingEntityParameterType
import app.simplecloud.simplecloud.restserver.base.parameter.SimpleBodyParameterType
import app.simplecloud.simplecloud.restserver.base.route.RouteMethodImpl

/**
 * Date: 03.05.22
 * Time: 18:45
 * @author Frederick Baier
 *
 */
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

    override fun addPathParameterType(name: String) {
        addParameter(PathParamParameterType(name))
    }

    override fun addBodyParameterType() {
        addParameter(SimpleBodyParameterType())
    }

    override fun addRequestingEntityParameterType() {
        addParameter(RequestingEntityParameterType())
    }

}