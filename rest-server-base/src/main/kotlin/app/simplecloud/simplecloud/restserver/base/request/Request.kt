package app.simplecloud.simplecloud.restserver.base.request

import app.simplecloud.simplecloud.restserver.base.exception.BodyParseException
import app.simplecloud.simplecloud.restserver.base.impl.RestServerBase
import app.simplecloud.simplecloud.restserver.base.route.RequestType
import app.simplecloud.simplecloud.restserver.base.user.RequestEntity

/**
 * Date: 14.03.22
 * Time: 10:01
 * @author Frederick Baier
 *
 */
interface Request {

    fun getRequestType(): RequestType

    fun getRequestPath(): String

    fun getRequestingEntity(): RequestEntity?

    fun getRequestBody(): String

    fun getPathParameter(name: String): String

    fun <T : Any> parseRequestBody(clazz: Class<T>): T {
        try {
            return RestServerBase.mapperExcludeIncoming.readValue(getRequestBody(), clazz)
        } catch (e: Exception) {
            throw BodyParseException(clazz.name)
        }
    }

}