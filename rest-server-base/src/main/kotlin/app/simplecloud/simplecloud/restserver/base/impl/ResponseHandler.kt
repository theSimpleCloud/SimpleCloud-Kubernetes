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
import app.simplecloud.simplecloud.api.future.exception.FutureOriginException
import app.simplecloud.simplecloud.restserver.base.exception.HttpException
import app.simplecloud.simplecloud.restserver.base.exception.MissingPermissionException
import app.simplecloud.simplecloud.restserver.base.exception.UnauthorizedException
import app.simplecloud.simplecloud.restserver.base.request.Request
import app.simplecloud.simplecloud.restserver.base.route.Route
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.CompletionException

/**
 * Date: 14.03.22
 * Time: 13:27
 * @author Frederick Baier
 *
 */
class ResponseHandler(
    private val context: Context,
    private val route: Route,
    private val request: Request
) {
    fun handle() {
        try {
            checkPermission()
            val response = this.route.handleRequest(this.request)
            handleResponse(response)
        } catch (ex: Exception) {
            ex.printStackTrace()
            handleException(ex)
        }
    }

    private fun checkPermission() {
        if (!route.hasPermission()) return
        val requestingEntity = this.request.getRequestingEntity() ?: throw UnauthorizedException()
        if (!requestingEntity.hasPermission(route.getPermission()).join()) throw MissingPermissionException()
    }

    private fun handleResponse(response: Any?) {
        if (response == null) {
            this.context.setResponseCode(500)
            writeResponseObject(ErrorResponseDto.fromException(NullPointerException("Null response")))
            return
        }
        this.context.setResponseCode(200)
        writeResponseObject(response)
    }

    private fun handleException(ex: Exception) {
        ex.printStackTrace()
        val exception = unpackException(ex)
        setErrorStatusCode(exception)
        writeErrorResponse(ErrorResponseDto.fromException(exception))
    }

    private fun unpackException(ex: Throwable): Throwable {
        if (ex is CompletionException) {
            return unpackException(ex.cause!!)
        }
        if (ex is InvocationTargetException) {
            return unpackException(ex.cause!!)
        }
        if (ex is FutureOriginException) {
            return unpackException(ex.cause!!)
        }
        return ex
    }

    private fun setErrorStatusCode(exception: Throwable) {
        if (exception is HttpException) {
            this.context.setResponseCode(exception.statusCode)
            return
        }
        this.context.setResponseCode(400)
    }

    private fun writeResponseObject(any: Any) {
        val successResponse = SuccessResponseDto(any)
        this.context.setResponseBody(RestServerBase.mapperExcludeOutgoing.writeValueAsString(successResponse))
    }

    private fun writeErrorResponse(errorResponseDto: ErrorResponseDto) {
        this.context.setResponseBody(RestServerBase.mapperExcludeOutgoing.writeValueAsString(errorResponseDto))
    }
}