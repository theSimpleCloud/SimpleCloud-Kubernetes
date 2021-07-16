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

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import eu.thesimplecloud.simplecloud.restserver.RestServer
import eu.thesimplecloud.simplecloud.restserver.annotation.RequestBody
import eu.thesimplecloud.simplecloud.restserver.annotation.RequestPathParam
import eu.thesimplecloud.simplecloud.restserver.annotation.RequestingUser
import eu.thesimplecloud.simplecloud.restserver.controller.MethodRoute
import eu.thesimplecloud.simplecloud.restserver.user.User
import io.ktor.application.*
import io.ktor.request.*
import java.lang.reflect.Parameter
import java.security.InvalidParameterException

class UncheckedRequestHandler(
    private val methodRoute: MethodRoute,
    private val call: ApplicationCall,
    private val user: User
) {

    private var bodyAsText: String? = null

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
            is RequestBody -> getRequestBody(parameter, annotation)
            is RequestPathParam -> {
                this.call.parameters[annotation.parameterName]
            }
            else -> throw IllegalArgumentException("Unknown annotation: ${annotation::class.java.name}")
        }
    }

    private suspend fun getRequestBody(
        parameter: MethodRoute.MethodRouteParameter,
        requestBodyAnnotation: RequestBody
    ): Any? {
        if (requestBodyAnnotation.types.isEmpty()) {
            return parseBodyToClass(parameter.parameterType)
        }
        return getTypedRequestBody(requestBodyAnnotation)
    }

    private suspend fun getTypedRequestBody(requestBodyAnnotation: RequestBody): Any? {
        val typeInBody = getTypeInBody()
        validateTypeInBody(typeInBody, requestBodyAnnotation.types)
        return getRequestBodyFromType(typeInBody, requestBodyAnnotation)
    }

    private suspend fun getRequestBodyFromType(
        typeInBody: String?,
        requestBodyAnnotation: RequestBody
    ): Any? {
        val index = requestBodyAnnotation.types.indexOf(typeInBody)
        val classToParseTo = requestBodyAnnotation.classes[index]
        return parseBodyToClass(classToParseTo.java)
    }

    private suspend fun <T : Any> parseBodyToClass(clazz: Class<T>): T? {
        return RestServer.mapperExcludeIncoming.readValue(getBodyAsText(), clazz)
    }

    private suspend fun getTypeInBody(): String? {
        val jsonBody = getBodyAsJsonNode()
        return jsonBody.get("type")?.asText()
    }

    private fun validateTypeInBody(typeInBody: String?, validTypes: Array<String>) {
        if (typeInBody == null || typeInBody !in validTypes)
            throw IllegalStateException("Invalid type")
    }

    private suspend fun getBodyAsJsonNode(): JsonNode {
        return RestServer.mapperExcludeIncoming.readValue(getBodyAsText(), JsonNode::class.java)
    }

    private fun checkAnnotationIsPresent(parameter: MethodRoute.MethodRouteParameter) {
        if (parameter.annotation == null) {
            throw InvalidParameterException(parameter.parameterType::class.java.name)
        }
    }

    private suspend fun getBodyAsText(): String {
        val bodyAsText = this.bodyAsText
        if (bodyAsText == null) {
            this.bodyAsText = this.call.receiveText()
        }
        return this.bodyAsText!!
    }


}
