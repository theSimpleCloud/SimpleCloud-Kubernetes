package app.simplecloud.simplecloud.restserver.base.route

import app.simplecloud.simplecloud.restserver.base.parameter.ParameterType
import app.simplecloud.simplecloud.restserver.base.vmethod.VirtualMethod

/**
 * Date: 14.03.22
 * Time: 09:58
 * @author Frederick Baier
 *
 */
interface RouteMethodBuilder {

    fun setVirtualMethod(virtualMethod: VirtualMethod): RouteMethodBuilder

    fun addParameter(parameterType: ParameterType): RouteMethodBuilder

    fun build(): RouteMethod



}