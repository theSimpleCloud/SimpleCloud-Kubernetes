package app.simplecloud.simplecloud.restserver.base.parameter

import app.simplecloud.simplecloud.restserver.base.request.Request

/**
 * Date: 14.03.22
 * Time: 10:47
 * @author Frederick Baier
 *
 */
class PathParamParameterType(
    private val pathParamName: String
) : ParameterType {

    override fun resolveValue(request: Request): String {
        return request.getPathParameter(this.pathParamName)
    }
}