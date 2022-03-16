package app.simplecloud.simplecloud.restserver.base.route

import app.simplecloud.simplecloud.restserver.base.parameter.ParameterType
import app.simplecloud.simplecloud.restserver.base.vmethod.VirtualMethod

/**
 * Date: 14.03.22
 * Time: 13:12
 * @author Frederick Baier
 *
 */
interface RouteMethod {

    fun getVirtualMethod(): VirtualMethod

    fun getParameters(): List<ParameterType>

}