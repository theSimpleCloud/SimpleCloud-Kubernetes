package app.simplecloud.simplecloud.restserver.base.route

import app.simplecloud.simplecloud.restserver.base.parameter.ParameterType
import app.simplecloud.simplecloud.restserver.base.vmethod.VirtualMethod

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