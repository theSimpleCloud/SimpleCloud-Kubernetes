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
import eu.thesimplecloud.simplecloud.restserver.annotation.exclude.WebExcludeAll
import eu.thesimplecloud.simplecloud.restserver.annotation.exclude.WebExcludeIncoming
import eu.thesimplecloud.simplecloud.restserver.annotation.exclude.WebExcludeOutgoing
import eu.thesimplecloud.simplecloud.restserver.annotation.introspector.AnnotationExcludeIntrospector
import eu.thesimplecloud.simplecloud.restserver.controller.ControllerHandler
import eu.thesimplecloud.simplecloud.restserver.controller.MethodRoute
import eu.thesimplecloud.simplecloud.restserver.jwt.JwtConfig
import eu.thesimplecloud.simplecloud.restserver.request.WebRequestHandler
import eu.thesimplecloud.simplecloud.restserver.service.AuthService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.File


/**
 * Created by IntelliJ IDEA.
 * Date: 22.06.2021
 * Time: 16:57
 * @author Frederick Baier
 */
class RestServer(port: Int) {

    private val authService = AuthService()

    private val server = embeddedServer(Netty, port) {
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
            post("/login") {
                call.respondText("Request uri: ${JwtConfig.makeToken("admin")}")
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
                        WebRequestHandler(methodRoute, call, authService).handleRequest()
                    }
                }
            }
        }
    }

    private fun createRequestWithoutAuth(methodRoute: MethodRoute) {
        this.server.application.routing {
            route(methodRoute.path, HttpMethod(methodRoute.requestType.name)) {
                handle {
                    WebRequestHandler(methodRoute, call, authService).handleRequest()
                }
            }
        }
    }

    companion object {

        val mapperExcludeOutgoing = ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .setAnnotationIntrospector(AnnotationExcludeIntrospector(WebExcludeOutgoing::class.java, WebExcludeAll::class.java))

        val mapperExcludeIncoming = ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .setAnnotationIntrospector(AnnotationExcludeIntrospector(WebExcludeIncoming::class.java, WebExcludeAll::class.java))

    }

}



fun main() {
    println(File(".").absolutePath)
    val restServer = RestServer(8000)
    ControllerHandler(restServer).registerController(TestController())
    /*
    embeddedServer(Netty, port = 8000) {
        install(Authentication) {
            jwt {
                verifier(
                    JwtConfig.verifier
                )
                validate { credential ->
                    //if (credential.payload.issuer.contains("ktor.io")) {
                    JWTPrincipal(credential.payload)
                    //} else {
                    //    null


                }
            }
        }

        routing {


            authenticate {
                get("/users") {
                    println(call.principal<JWTPrincipal>()!!.getClaim("id", String::class))
                    call.respondText("hallo123")
                }

            }
            post("/login") {
                call.respondText("Request uri: ${JwtConfig.makeToken("1")}")
            }

        }
    }.start(wait = true)

     */
}