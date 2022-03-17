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

package app.simplecloud.simplecloud.restserver.base.impl

import app.simplecloud.rest.Context
import app.simplecloud.rest.Handler
import app.simplecloud.rest.RequestMethod
import app.simplecloud.rest.RestServerFactory
import app.simplecloud.simplecloud.restserver.base.RestServer
import app.simplecloud.simplecloud.restserver.base.auth.JwtTokenHandler
import app.simplecloud.simplecloud.restserver.base.exception.UnauthorizedException
import app.simplecloud.simplecloud.restserver.base.exclude.annotation.WebExcludeAll
import app.simplecloud.simplecloud.restserver.base.exclude.annotation.WebExcludeIncoming
import app.simplecloud.simplecloud.restserver.base.exclude.annotation.WebExcludeOutgoing
import app.simplecloud.simplecloud.restserver.base.exclude.introspector.AnnotationExcludeIntrospector
import app.simplecloud.simplecloud.restserver.base.route.Route
import app.simplecloud.simplecloud.restserver.base.service.AuthService
import app.simplecloud.simplecloud.restserver.base.user.RequestEntity
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import kotlinx.coroutines.*
import kotlinx.coroutines.future.await
import net.bytebuddy.implementation.bind.annotation.Super


/**
 * Created by IntelliJ IDEA.
 * Date: 22.06.2021
 * Time: 16:57
 * @author Frederick Baier
 */
class RestServerBase(
    @Volatile
    private var authService: AuthService,
    private val port: Int
) : RestServer {

    private val server = RestServerFactory.createServer(port)
    private val coroutineScope: CoroutineScope

    init {
        val threadContext = newSingleThreadContext("Rest-Server")
        val exceptionHandler = CoroutineExceptionHandler { _, throwable -> throwable.printStackTrace() }
        //the [SupervisorJob] is necessary to handle exceptions. Without it the coroutine would crash with the first error
        this.coroutineScope = CoroutineScope(threadContext + exceptionHandler + SupervisorJob())
    }

    override fun setAuthService(authService: AuthService) {
        this.authService = authService
    }

    override fun registerRoute(route: Route) {
        this.server.registerRoute(
            RequestMethod.valueOf(route.getRequestType().name),
            route.getPath(),
            object : Handler {
                override fun handle(context: Context) {
                    coroutineScope.launch {
                        handleIncomingRequest(route, context)
                    }
                }
            }
        )
    }

    override fun unregisterRoute(route: Route) {
        TODO("Not yet implemented")
    }

    private suspend fun handleIncomingRequest(route: Route, context: Context) {
        val requestBody = context.getRequestBody()
        val request = RequestCreator(route, context, requestBody, this.authService).createRequest()
        ResponseHandler(context, route, request).handle()
    }

    fun shutdown() {
        this.server.stop()
    }

    companion object {

        val mapperExcludeOutgoing = ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .setAnnotationIntrospector(
                AnnotationExcludeIntrospector(
                    WebExcludeOutgoing::class.java,
                    WebExcludeAll::class.java
                )
            )

        val mapperExcludeIncoming = ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .setAnnotationIntrospector(
                AnnotationExcludeIntrospector(
                    WebExcludeIncoming::class.java,
                    WebExcludeAll::class.java
                )
            )

    }

}