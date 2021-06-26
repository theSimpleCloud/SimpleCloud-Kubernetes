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

package eu.thesimplecloud.simplecloud.restserver.request

import eu.thesimplecloud.simplecloud.restserver.RestServer
import eu.thesimplecloud.simplecloud.restserver.annotation.RequestBody
import eu.thesimplecloud.simplecloud.restserver.annotation.RequestPathParam
import eu.thesimplecloud.simplecloud.restserver.annotation.RequestingUser
import eu.thesimplecloud.simplecloud.restserver.controller.MethodRoute
import eu.thesimplecloud.simplecloud.restserver.user.User
import io.ktor.application.*
import io.ktor.request.*
import java.security.InvalidParameterException

class UncheckedRequestHandler(
    private val methodRoute: MethodRoute,
    private val call: ApplicationCall,
    private val user: User
) {

    suspend fun handleRequest(): Any? {
        val parameterValues = mapParameters()
        return invokeMethodWithParameters(parameterValues)
    }

    private fun invokeMethodWithParameters(parameterValues: List<Any?>): Any? {
        return methodRoute.invokeMethodWithArgs(parameterValues)
    }

    private suspend fun mapParameters(): List<Any?> {
        return this.methodRoute.parameters.map { getParameterValue(it) }
    }

    private suspend fun getParameterValue(parameter: MethodRoute.MethodRouteParameter): Any? {
        if (parameter.parameterType == ApplicationCall::class.java)
            return this.call
        checkAnnotationIsPresent(parameter)
        val annotation = parameter.annotation!!
        return when (annotation) {
            is RequestingUser -> this.user
            is RequestBody -> RestServer.mapperExcludeIncoming.readValue(this.call.receiveText(), parameter.parameterType)
            is RequestPathParam -> {
                this.call.parameters[annotation.parameterName]
            }
            else -> throw IllegalArgumentException("Unknown annotation: ${annotation::class.java.name}")
        }
    }

    private fun checkAnnotationIsPresent(parameter: MethodRoute.MethodRouteParameter) {
        if (parameter.annotation == null) {
            throw InvalidParameterException(parameter.parameterType::class.java.name)
        }
    }


}
