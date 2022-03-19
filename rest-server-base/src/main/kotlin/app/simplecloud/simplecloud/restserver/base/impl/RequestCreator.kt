package app.simplecloud.simplecloud.restserver.base.impl

import app.simplecloud.rest.Context
import app.simplecloud.simplecloud.restserver.base.request.Request
import app.simplecloud.simplecloud.restserver.base.request.RequestImpl
import app.simplecloud.simplecloud.restserver.base.route.Route
import app.simplecloud.simplecloud.restserver.base.service.AuthService
import kotlinx.coroutines.future.await

/**
 * Date: 14.03.22
 * Time: 12:21
 * @author Frederick Baier
 *
 */
class RequestCreator(
    private val route: Route,
    private val context: Context,
    private val requestBody: String,
    private val authService: AuthService
) {

    suspend fun createRequest(): Request {
        val entity = runCatching { this.authService.getRequestEntityFromContext(this.context).await() }.getOrNull()
        println("RequestCreator found an entity ${entity}")
        val pathParameters = this.context.getRequestPathParameters()
        return RequestImpl(
            this.route.getRequestType(),
            this.route.getPath(),
            this.requestBody,
            entity,
            pathParameters
        )
    }

}