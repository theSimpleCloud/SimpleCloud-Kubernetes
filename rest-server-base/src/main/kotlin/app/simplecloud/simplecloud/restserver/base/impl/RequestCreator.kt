package app.simplecloud.simplecloud.restserver.base.impl

import app.simplecloud.rest.Context
import app.simplecloud.simplecloud.api.permission.PermissionEntity
import app.simplecloud.simplecloud.restserver.base.request.Request
import app.simplecloud.simplecloud.restserver.base.request.RequestImpl
import app.simplecloud.simplecloud.restserver.base.route.Route
import app.simplecloud.simplecloud.restserver.base.service.AuthService
import org.apache.logging.log4j.LogManager

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
        val entity = getRequestEntity()
        logger.info("Received a request with entity ${entity} on ${this.route.getPath()}")
        val pathParameters = this.context.getRequestPathParameters()
        return RequestImpl(
            this.route.getRequestType(),
            this.route.getPath(),
            this.requestBody,
            entity,
            pathParameters
        )
    }

    private suspend fun getRequestEntity(): PermissionEntity? {
        return try {
            this.authService.getRequestEntityFromContext(this.context)
        } catch (e: Exception) {
            logger.error("An error occurred while resolving a request entity. Continuing with null.", e)
            null
        }
    }

    companion object {
        private val logger = LogManager.getLogger(RequestCreator::class.java)
    }

}