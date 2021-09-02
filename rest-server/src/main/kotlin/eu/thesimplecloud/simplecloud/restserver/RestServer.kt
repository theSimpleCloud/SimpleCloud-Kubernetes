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

package eu.thesimplecloud.simplecloud.restserver

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.google.inject.Inject
import com.google.inject.Injector
import eu.thesimplecloud.simplecloud.restserver.annotation.exclude.WebExcludeAll
import eu.thesimplecloud.simplecloud.restserver.annotation.exclude.WebExcludeIncoming
import eu.thesimplecloud.simplecloud.restserver.annotation.exclude.WebExcludeOutgoing
import eu.thesimplecloud.simplecloud.restserver.annotation.introspector.AnnotationExcludeIntrospector
import eu.thesimplecloud.simplecloud.restserver.controller.ControllerHandler
import eu.thesimplecloud.simplecloud.restserver.controller.MethodRoute
import eu.thesimplecloud.simplecloud.restserver.defaultcontroller.v1.*
import eu.thesimplecloud.simplecloud.restserver.jwt.JwtConfig
import eu.thesimplecloud.simplecloud.restserver.request.WebRequestHandler
import eu.thesimplecloud.simplecloud.restserver.service.IAuthService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import io.ktor.utils.io.*
import java.util.concurrent.TimeUnit


/**
 * Created by IntelliJ IDEA.
 * Date: 22.06.2021
 * Time: 16:57
 * @author Frederick Baier
 */
class RestServer @Inject constructor(
    private val authService: IAuthService,
    private val injector: Injector
) {


    val controllerHandler = ControllerHandler(this, injector)

    private val server = embeddedServer(Netty, 8008) {
        val client = HttpClient(CIO)
        install(CORS) {
            method(HttpMethod.Options)
            method(HttpMethod.Get)
            method(HttpMethod.Post)
            method(HttpMethod.Put)
            method(HttpMethod.Delete)
            method(HttpMethod.Patch)
            header(HttpHeaders.AccessControlAllowHeaders)
            header(HttpHeaders.ContentType)
            header(HttpHeaders.AccessControlAllowOrigin)
            allowCredentials = true
            anyHost()
        }
        install(Authentication) {
            jwt {
                verifier(
                    JwtConfig.verifier
                )
                validate { credential ->
                    JWTPrincipal(credential.payload)
                }
            }
        }
        routing {
            route("ui", HttpMethod.Get) {
                handle {
                    val uri = this.context.request.uri
                    println("call on ${uri}")
                    val replacedUri = uri.replace("/ui", "")
                    println("calling https://dashboard.thesimplecloud.eu/${replacedUri}")
                    val response = client.request<HttpResponse>("https://dashboard.thesimplecloud.eu/${replacedUri}")
                    println(response.status)
                    val proxiedHeaders = response.headers
                    val location = proxiedHeaders[HttpHeaders.Location]
                    val contentType = proxiedHeaders[HttpHeaders.ContentType]
                    val contentLength = proxiedHeaders[HttpHeaders.ContentLength]

                    if (location != null) {
                        call.response.header(HttpHeaders.Location, location)
                    }
                    println("calling respond")
                    /*
                    call.respond(object : OutgoingContent.WriteChannelContent() {
                        override val contentLength: Long? = contentLength?.toLong()
                        override val contentType: ContentType? = contentType?.let { ContentType.parse(it) }

                        //                override val headers: Headers = proxiedHeaders
                        override val headers: Headers = Headers.build {
                            appendAll(proxiedHeaders)
                        }

                        override val status: HttpStatusCode? = response.status
                        override suspend fun writeTo(channel: ByteWriteChannel) {
                            response.content.copyAndClose(channel)
                        }
                    })

                     */

                    call.respond(object : OutgoingContent.WriteChannelContent() {
                        override val contentLength: Long? = contentLength?.toLong()
                        override val contentType: ContentType? = contentType?.let { ContentType.parse(it) }
                        override val headers: Headers = Headers.build {
                            appendAll(proxiedHeaders.filter { key, _ ->
                                !key.equals(
                                    HttpHeaders.ContentType,
                                    ignoreCase = true
                                ) && !key.equals(HttpHeaders.ContentLength, ignoreCase = true)
                            })
                        }
                        override val status: HttpStatusCode? = response.status
                        override suspend fun writeTo(channel: ByteWriteChannel) {
                            response.content.copyAndClose(channel)
                        }
                    })
                }
            }
        }
    }.start(wait = false)

    fun registerMethodRoute(methodRoute: MethodRoute) {
        if (methodRoute.permission.isEmpty()) {
            createRequestWithoutAuth(methodRoute)
            return
        }
        createRequestWithAuth(methodRoute)
    }

    private fun createRequestWithAuth(methodRoute: MethodRoute) {
        this.server.application.routing {
            route(methodRoute.path, HttpMethod(methodRoute.requestType.name)) {
                authenticate {
                    handle {
                        handleIncomingRequest(methodRoute, call)
                    }
                }
            }
        }
    }

    private fun createRequestWithoutAuth(methodRoute: MethodRoute) {
        this.server.application.routing {
            route(methodRoute.path, HttpMethod(methodRoute.requestType.name)) {
                handle {
                    handleIncomingRequest(methodRoute, call)
                }
            }
        }
    }

    private suspend fun handleIncomingRequest(methodRoute: MethodRoute, call: ApplicationCall) {
        try {
            val requestBody = call.receiveText()
            WebRequestHandler(methodRoute, call, requestBody, authService).handleRequest()
        } catch (ex: RequestAlreadyConsumedException) {
            //ignored because the request cannot be already consumed here
        }
    }

    fun shutdown() {
        val environment = this.server.application.environment
        if (environment is ApplicationEngineEnvironment) {
            environment.stop()
            this.server.stop(3000, 5000)
        }
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