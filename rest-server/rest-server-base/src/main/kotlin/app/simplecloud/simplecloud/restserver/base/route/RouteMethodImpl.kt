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
import app.simplecloud.simplecloud.restserver.api.route.RouteMethod
import app.simplecloud.simplecloud.restserver.api.vmethod.VirtualMethod

/**
 * Date: 14.03.22
 * Time: 13:13
 * @author Frederick Baier
 *
 */
class RouteMethodImpl(
    private val virtualMethod: VirtualMethod,
    private val parameters: List<ParameterType>
) : RouteMethod {
    override fun getVirtualMethod(): VirtualMethod {
        return this.virtualMethod
    }

    override fun getParameters(): List<ParameterType> {
        return this.parameters
    }
}