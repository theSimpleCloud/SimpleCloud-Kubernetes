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

package app.simplecloud.simplecloud.module.api.impl.resourcedefinition.schema

import app.simplecloud.simplecloud.api.resourcedefinition.Definition
import app.simplecloud.simplecloud.api.resourcedefinition.limitation.*
import app.simplecloud.simplecloud.api.resourcedefinition.limitation.annotation.*
import app.simplecloud.simplecloud.api.resourcedefinition.limitation.annotation.Optional
import app.simplecloud.simplecloud.api.utils.getEnumValues
import java.lang.reflect.Field
import java.util.*


/**
 * Date: 19.01.23
 * Time: 18:07
 * @author Frederick Baier
 *
 */
class SchemaCreator(
    private val clazz: Class<*>,
    private val isOptional: Boolean,
    private val annotations: List<Annotation>,
) {

    fun createSchema(): Definition {
        if (isPrimitiveType()) {
            return createPrimitiveTypeDefinition()
        }
        if (isEnumType()) {
            return createEnumType()
        }
        if (this.clazz.isArray) {
            return createArrayDefinition()
        }
        return customTypeDefinition()
    }

    private fun createArrayDefinition(): Definition {
        val componentType = this.clazz.componentType
        val innerTypeDefinition = SchemaCreator(componentType, false, emptyList()).createSchema()
        return Definition("array", this.isOptional, emptyList(), mapOf("innerType" to innerTypeDefinition))
    }

    private fun createEnumType(): Definition {
        val enumClass = this.clazz as Class<out Enum<*>>
        return Definition(
            "string",
            this.isOptional,
            listOf(StringValuesLimitation(enumClass.getEnumValues())),
            emptyMap()
        )
    }

    private fun isEnumType(): Boolean {
        return this.clazz.isEnum
    }

    private fun customTypeDefinition(): Definition {
        val fields = getFields(this.clazz)
        val map = fields.associate { it.name to createDefinitionForField(it) }
        return Definition("object", this.isOptional, emptyList(), map)
    }

    private fun createPrimitiveTypeDefinition(): Definition {
        val limitations = this.annotations.mapNotNull { convertAnnotationToLimitation(it) }
        val className = this.clazz.simpleName.lowercase()
        if (className == "integer") {
            return Definition("int", this.isOptional, limitations, emptyMap())
        }
        return Definition(className, this.isOptional, limitations, emptyMap())
    }

    private fun convertAnnotationToLimitation(annotation: Annotation): Limitation? {
        if (!isAnnotationValidWithType(annotation)) {
            throw InvalidAnnotationForTypeException("Annotation ${annotation.annotationClass.qualifiedName} is not compatible with type ${this.clazz.name}")
        }
        return when (annotation) {
            is DoubleNumberValues -> NumberValuesLimitation(annotation.values.toList())
            is IntNumberValues -> NumberValuesLimitation(annotation.values.toList())
            is LongNumberValues -> NumberValuesLimitation(annotation.values.toList())
            is StringValues -> StringValuesLimitation(annotation.values.toList())
            is MaxValue -> MaxValueLimitation(annotation.value)
            is MinValue -> MinValueLimitation(annotation.value)
            is StringMaxLength -> StringMaxLengthLimitation(annotation.value)
            is StringMinLength -> StringMinLengthLimitation(annotation.value)
            else -> {
                null
            }
        }
    }

    private fun isAnnotationValidWithType(annotation: Annotation): Boolean {
        return when (annotation) {
            is DoubleNumberValues -> this.clazz == Double::class.java || this.clazz == Double::class.javaPrimitiveType
            is IntNumberValues -> this.clazz == Int::class.java || this.clazz == Int::class.javaPrimitiveType
            is LongNumberValues -> this.clazz == Long::class.java || this.clazz == Long::class.javaPrimitiveType
            is StringValues -> this.clazz == String::class.java
            is MaxValue -> isSupportedNumberType(this.clazz)
            is MinValue -> isSupportedNumberType(this.clazz)
            is StringMaxLength -> this.clazz == String::class.java
            is StringMinLength -> this.clazz == String::class.java
            else -> {
                //unknown annotation, no check needed
                true
            }
        }
    }

    private fun isSupportedNumberType(clazz: Class<*>): Boolean {
        return clazz == Double::class.java || clazz == Double::class.javaPrimitiveType ||
                clazz == Int::class.java || clazz == Int::class.javaPrimitiveType ||
                clazz == Long::class.java || clazz == Long::class.javaPrimitiveType


    }

    private fun isPrimitiveType(): Boolean {
        val primitiveTypes = listOf(
            Boolean::class.javaObjectType,
            Short::class.javaObjectType,
            Int::class.javaObjectType,
            Long::class.javaObjectType,
            Float::class.javaObjectType,
            Double::class.javaObjectType,
            String::class.javaObjectType,
            Boolean::class.javaPrimitiveType!!,
            Short::class.javaPrimitiveType!!,
            Int::class.javaPrimitiveType!!,
            Long::class.javaPrimitiveType!!,
            Float::class.javaPrimitiveType!!,
            Double::class.javaPrimitiveType!!,
            UUID::class.java,
        )
        return primitiveTypes.contains(this.clazz)
    }

    private fun createDefinitionForField(field: Field): Definition {
        return SchemaCreator(
            field.type,
            field.isAnnotationPresent(Optional::class.java),
            field.annotations.toList()
        ).createSchema()
    }

    private fun getFields(inputClazz: Class<*>): List<Field> {
        val fields: MutableList<Field> = ArrayList()
        var clazz: Class<*> = inputClazz
        while (clazz != Any::class.java) {
            fields.addAll(clazz.declaredFields)
            clazz = clazz.superclass
        }
        return fields
    }

    class InvalidAnnotationForTypeException(msg: String) : Exception(msg)

}
