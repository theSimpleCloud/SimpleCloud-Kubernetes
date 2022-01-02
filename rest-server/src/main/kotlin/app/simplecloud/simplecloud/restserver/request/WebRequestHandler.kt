/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package app.simplecloud.simplecloud.restserver.request

import app.simplecloud.simplecloud.api.utils.future.FutureOriginException
import app.simplecloud.simplecloud.restserver.RestServer
import app.simplecloud.simplecloud.restserver.controller.MethodRoute
import app.simplecloud.simplecloud.restserver.exception.HttpException
import app.simplecloud.simplecloud.restserver.exception.MissingPermissionException
import app.simplecloud.simplecloud.restserver.exception.NotAuthenticatedException
import app.simplecloud.simplecloud.restserver.service.AuthService
import app.simplecloud.simplecloud.restserver.user.EmptyUser
import app.simplecloud.simplecloud.restserver.user.User
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import kotlinx.coroutines.future.await
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.CompletionException

/**
 * Created by IntelliJ IDEA.
 * Date: 23.06.2021
 * Time: 14:21
 * @author Frederick Baier
 */
class WebRequestHandler(
    private val methodRoute: MethodRoute,
    private val call: ApplicationCall,
    private val requestBody: String,
    private val authService: AuthService
) {

    suspend fun handleRequest() {
        try {
            handleRequest0()
        } catch (ex: Exception) {
            handleException(ex)
        }
    }

    private suspend fun handleException(ex: Exception) {
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
            writeStatusCode(exception.statusCode)
            return
        }
        writeStatusCode(HttpStatusCode.BadRequest)
    }

    private fun writeStatusCode(statusCode: HttpStatusCode) {
        call.response.status(statusCode)
    }

    private suspend fun writeResponseObject(any: Any) {
        val successResponse = SuccessResponseDto(any)
        call.respondText(RestServer.mapperExcludeOutgoing.writeValueAsString(successResponse))
    }

    private suspend fun writeErrorResponse(errorResponseDto: ErrorResponseDto) {
        call.respondText(RestServer.mapperExcludeOutgoing.writeValueAsString(errorResponseDto))
    }

    private suspend fun handleRequest0() {
        val user = getUserOrEmptyUser()
        checkUserPermission(user)
        handleRequestUnchecked(user)
    }

    private suspend fun handleRequestUnchecked(user: User) {
        val response = UncheckedRequestHandler(methodRoute, call, user, requestBody).handleRequest()
        handleResponse(response)
    }

    private suspend fun handleResponse(response: Any?) {
        if (response == null) {
            writeStatusCode(HttpStatusCode.InternalServerError)
            writeResponseObject(ErrorResponseDto.fromException(NullPointerException("Null response")))
            return
        }
        writeStatusCode(HttpStatusCode.OK)
        writeResponseObject(response)
    }

    private fun checkUserPermission(user: User) {
        if (!user.hasPermission(this.methodRoute.permission)) {
            blockAccess()
        }
    }

    private suspend fun getUserOrEmptyUser(): User {
        try {
            return this.authService.getUserFromCall(this.call).await()
        } catch (e: NotAuthenticatedException) {
            return EmptyUser
        }
    }

    private fun blockAccess() {
        throw MissingPermissionException()
    }


}