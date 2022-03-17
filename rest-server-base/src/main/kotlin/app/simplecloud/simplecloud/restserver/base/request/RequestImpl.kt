package app.simplecloud.simplecloud.restserver.base.request

import app.simplecloud.simplecloud.restserver.base.route.RequestType
import app.simplecloud.simplecloud.restserver.base.user.RequestEntity

/**
 * Date: 14.03.22
 * Time: 11:37
 * @author Frederick Baier
 *
 */
class RequestImpl(
    private val requestType: RequestType,
    private val requestPath: String,
    private val requestBody: String,
    private val entity: RequestEntity?,
    private val pathParameters: Map<String, String>
) : Request {
    override fun getRequestType(): RequestType {
        return this.requestType
    }

    override fun getRequestPath(): String {
        return this.requestPath
    }

    override fun getRequestingEntity(): RequestEntity? {
        return this.entity
    }

    override fun getRequestBody(): String {
        return this.requestBody
    }

    override fun getPathParameter(name: String): String {
        return this.pathParameters[name] ?: throw NoSuchElementException("No Parameter for '${name}' was provided")
    }
}