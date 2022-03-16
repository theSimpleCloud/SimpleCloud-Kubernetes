package app.simplecloud.simplecloud.restserver.base.parameter

import app.simplecloud.simplecloud.restserver.base.request.Request

/**
 * Date: 14.03.22
 * Time: 09:59
 * @author Frederick Baier
 *
 */
class RequestingUserParameterType : ParameterType {

    override fun resolveValue(request: Request): Any? {
        return request.getRequestingEntity()
    }
}