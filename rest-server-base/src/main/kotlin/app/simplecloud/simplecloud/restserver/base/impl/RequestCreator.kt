/*
 * SimpleCloud is a software for administrating a minecraft server network.
 * Copyright (C) 2022 Frederick Baier & Philipp Eistrach
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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