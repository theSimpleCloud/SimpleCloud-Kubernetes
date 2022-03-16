package app.simplecloud.simplecloud.restserver.base.impl

import app.simplecloud.simplecloud.restserver.base.request.Request
import app.simplecloud.simplecloud.restserver.base.request.RequestImpl
import app.simplecloud.simplecloud.restserver.base.route.Route
import app.simplecloud.simplecloud.restserver.base.service.AuthService
import io.ktor.application.*
import kotlinx.coroutines.future.await

/**
 * Date: 14.03.22
 * Time: 12:21
 * @author Frederick Baier
 *
 */
class RequestCreator(
    private val route: Route,
    private val call: ApplicationCall,
    private val requestBody: String,
    private val authService: AuthService
) {

    suspend fun createRequest(): Request {
        val entity = this.authService.getRequestEntityFromCall(call).await()
        val pathParameters = call.parameters.entries()
            .map { it.key to it.value.firstOrNull() }
            .filter { it.second != null }
            .toMap() as Map<String, String>
        return RequestImpl(
            this.route.getRequestType(),
            this.route.getPath(),
            this.requestBody,
            entity,
            pathParameters
        )
    }

}