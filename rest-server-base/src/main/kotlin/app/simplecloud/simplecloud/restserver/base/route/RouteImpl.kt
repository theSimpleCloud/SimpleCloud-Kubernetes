package app.simplecloud.simplecloud.restserver.base.route

import app.simplecloud.simplecloud.restserver.base.parameter.ParameterType
import app.simplecloud.simplecloud.restserver.base.request.Request
import app.simplecloud.simplecloud.restserver.base.vmethod.VirtualMethod

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
        return this.virtualMethod.invoke(methodValues.toTypedArray())
    }



}