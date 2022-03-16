package app.simplecloud.simplecloud.restserver.base.impl

import app.simplecloud.simplecloud.restserver.base.exception.HttpException
import app.simplecloud.simplecloud.restserver.base.request.Request
import app.simplecloud.simplecloud.restserver.base.route.Route
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.CompletionException

/**
 * Date: 14.03.22
 * Time: 13:27
 * @author Frederick Baier
 *
 */
class ResponseHandler(
    private val call: ApplicationCall,
    private val route: Route,
    private val request: Request
) {
    suspend fun handle() {
        try {
            val response = this.route.handleRequest(this.request)
            handleResponse(response)
        } catch (ex: Exception) {
            ex.printStackTrace()
            handleException(ex)
        }
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
        //if (ex is FutureOriginException) {
        //    return unpackException(ex.cause!!)
        //}
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
        call.respondText(RestServerBase.mapperExcludeOutgoing.writeValueAsString(successResponse))
    }

    private suspend fun writeErrorResponse(errorResponseDto: ErrorResponseDto) {
        call.respondText(RestServerBase.mapperExcludeOutgoing.writeValueAsString(errorResponseDto))
    }
}