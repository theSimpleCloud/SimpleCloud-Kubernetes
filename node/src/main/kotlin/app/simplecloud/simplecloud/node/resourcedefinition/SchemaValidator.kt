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

package app.simplecloud.simplecloud.node.resourcedefinition

import app.simplecloud.simplecloud.api.resourcedefinition.Definition
import eu.thesimplecloud.jsonlib.JsonLib

/**
 * Date: 22.01.23
 * Time: 12:17
 * @author Frederick Baier
 *
 */
class SchemaValidator(
    private val schema: Definition,
    private val jsonLib: JsonLib,
) {

    fun validate() {
        if (schema.type != "object")
            throw IllegalArgumentException("Schema must start with type 'object'. Found: ${schema.type}")
        validateSchema(this.schema, this.jsonLib, "(root)")
    }

    private fun validateSchema(schema: Definition, json: JsonLib, propertyPath: String) {
        if (schema.type == "object") {
            validateObjectSchema(schema, json, propertyPath)
            return
        }
        if (schema.type == "array") {
            validateArraySchema(schema, json, propertyPath)
            return
        }
        validatePrimitive(schema, json, propertyPath)
    }

    private fun validateArraySchema(schema: Definition, json: JsonLib, propertyPath: String) {
        if (!json.jsonElement.isJsonArray) {
            throw SchemaValidationException("Expected Array at position $propertyPath")
        }

        val properties = schema.properties
        val innerTypeDefinition = properties["innerType"]!!
        val jsonArray = json.jsonElement.asJsonArray
        jsonArray.forEachIndexed { index, innerJsonElement ->
            validateSchema(innerTypeDefinition, JsonLib.fromJsonElement(innerJsonElement), "${propertyPath}.[${index}]")
        }
    }

    private fun validatePrimitive(schema: Definition, json: JsonLib, propertyPath: String) {
        if (!json.jsonElement.isJsonPrimitive)
            throw SchemaValidationException("Expected Primitive (${schema.type}) at position $propertyPath")
        val parsedPrimitive = when (schema.type) {
            "int" -> {
                validateAndParseType(json, Int::class.java, propertyPath)
            }

            "double" -> {
                validateAndParseType(json, Double::class.java, propertyPath)
            }

            "long" -> {
                validateAndParseType(json, Long::class.java, propertyPath)
            }

            "string" -> {
                validateAndParseType(json, String::class.java, propertyPath)
            }

            "boolean" -> {
                validateAndParseType(json, Boolean::class.java, propertyPath)
            }

            else ->
                throw IllegalArgumentException("Invalid schema: invalid type '${schema.type}'")
        }
        val limitations = schema.limitations
        if (limitations.any { !it.meetsLimitation(parsedPrimitive) }) {
            throw SchemaValidationException("Value ${json.getAsJsonString()} is not meeting the limitations at position '$propertyPath'")
        }
    }

    private fun <T> validateAndParseType(json: JsonLib, type: Class<T>, propertyPath: String): T {
        try {
            return json.getObject(type)
        } catch (e: Exception) {
            throw SchemaValidationException("Invalid type: expected type '${type.simpleName}' at position '$propertyPath' but value was: ${json.getAsJsonString()}")
        }
    }

    private fun validateObjectSchema(schema: Definition, json: JsonLib, propertyPath: String) {
        if (!json.jsonElement.isJsonObject) {
            throw SchemaValidationException("Expected Object at position '${propertyPath}'")
        }
        val properties = schema.properties
        for ((propertyName, definition) in properties) {
            val innerJson = json.getProperty(propertyName)
            if (innerJson == null) {
                if (!definition.isOptional) {
                    throw SchemaValidationException("Expected Property '$propertyPath.$propertyName' does not exist")
                } else
                    continue
            }
            validateSchema(definition, innerJson, "$propertyPath.$propertyName")
        }
    }

    class SchemaValidationException(msg: String) : Exception(msg)


}