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

package app.simplecloud.simplecloud.restserver.base.parameter

import app.simplecloud.simplecloud.restserver.api.ParameterType
import app.simplecloud.simplecloud.restserver.api.Request
import com.fasterxml.jackson.databind.JsonNode

/**
 * Date: 14.03.22
 * Time: 10:49
 * @author Frederick Baier
 *
 */
class RequestBodyParameterType(
    private val types: Array<String>,
    private val classes: Array<Class<*>>,
) : ParameterType {

    init {
        require(classes.isNotEmpty()) { "Classes must not be empty" }
    }

    override fun resolveValue(request: Request): Any {
        if (this.types.isEmpty()) {
            return request.parseRequestBody(this.classes.first())
        }
        return getTypedRequestBody(request)
    }

    private fun getTypedRequestBody(request: Request): Any {
        val typeInBody = getTypeInBody(request)
        validateTypeInBody(typeInBody)
        return getRequestBodyFromType(typeInBody!!, request)
    }

    private fun getRequestBodyFromType(typeInBody: String, request: Request): Any {
        val index = this.types.indexOf(typeInBody)
        val classToParseTo = this.classes[index]
        return request.parseRequestBody(classToParseTo)
    }

    private fun getTypeInBody(request: Request): String? {
        val jsonBody = getBodyAsJsonNode(request)
        return jsonBody.get("type")?.asText()
    }

    private fun validateTypeInBody(typeInBody: String?) {
        if (typeInBody == null || typeInBody !in this.types)
            throw IllegalStateException("Invalid type")
    }

    private fun getBodyAsJsonNode(request: Request): JsonNode {
        return request.parseRequestBody(JsonNode::class.java)
    }

    companion object {

        fun singleClass(clazz: Class<*>): RequestBodyParameterType {
            return RequestBodyParameterType(emptyArray(), arrayOf(clazz))
        }

    }

}