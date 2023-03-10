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
import app.simplecloud.simplecloud.api.resourcedefinition.limitation.annotation.MaxValue
import app.simplecloud.simplecloud.api.resourcedefinition.limitation.annotation.MinValue
import app.simplecloud.simplecloud.api.resourcedefinition.limitation.annotation.Optional
import app.simplecloud.simplecloud.api.resourcedefinition.limitation.annotation.StringMinLength
import app.simplecloud.simplecloud.module.api.impl.resourcedefinition.schema.SchemaCreator
import eu.thesimplecloud.jsonlib.JsonLib
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

/**
 * Date: 22.01.23
 * Time: 13:11
 * @author Frederick Baier
 *
 */
class SchemaValidatorTest {

    private var schema: Definition? = null

    @BeforeEach
    fun setUp() {
        this.schema = null
    }

    private inline fun <reified T> givenSchemaWith() {
        this.schema = SchemaCreator(T::class.java, false, emptyList()).createSchema()
    }

    private fun assertValidateFails(jsonLib: JsonLib) {
        val schema = this.schema ?: throw IllegalStateException("Schema not set")
        Assertions.assertThrows(SchemaValidator.SchemaValidationException::class.java) {
            SchemaValidator(
                schema,
                jsonLib
            ).validate()
        }
    }

    private fun assertValid(jsonLib: JsonLib) {
        val schema = this.schema ?: throw IllegalStateException("Schema not set")
        SchemaValidator(
            schema,
            jsonLib
        ).validate()
    }

    @Test
    fun schema_validateWithEmptyJson_willFail() {
        givenSchemaWith<SimpleDefinitionClass>()
        assertValidateFails(JsonLib.empty())
    }

    @ParameterizedTest
    @ValueSource(strings = ["test", "5", "d", "5.3"])
    fun schema_validateWithMissingProperty_willFail(parameter: String) {
        givenSchemaWith<SimpleDefinitionClass>()
        assertValidateFails(JsonLib.empty().append("string", parameter))
    }

    @Test
    fun schema_validateWithWrongJson2_willFail() {
        givenSchemaWith<SimpleDefinitionClass>()
        assertValidateFails(JsonLib.empty().append("string", "3"))
    }

    @Test
    fun schema_validateWithValidJsonButNumberTooLow_willFail() {
        givenSchemaWith<SimpleDefinitionClass>()
        assertValidateFails(JsonLib.empty().append("int", "-6"))
    }

    @Test
    fun schema_validateWithValidJson_willNotFail() {
        givenSchemaWith<SimpleDefinitionClass>()
        assertValid(JsonLib.empty().append("int", 0))
    }


    @Test
    fun schemaWithOptional_validateWithCompleteJson() {
        givenSchemaWith<DefinitionWithOptionalValue>()
        assertValid(JsonLib.empty().append("int", 0).append("string", "test"))
    }

    @Test
    fun schemaWithOptional_validateWithIncompleteJson() {
        givenSchemaWith<DefinitionWithOptionalValue>()
        assertValid(JsonLib.empty().append("int", 0))
    }

    @Test
    fun schemaWithArray_validateWithNoArray_willFail() {
        givenSchemaWith<DefinitionWithArray>()
        assertValidateFails(JsonLib.empty().append("array", 0))
    }

    @Test
    fun schemaWithArray_validateWithArray() {
        givenSchemaWith<DefinitionWithArray>()
        println(JsonLib.fromObject(this.schema!!).getAsJsonString())
        assertValid(JsonLib.empty().append("array", arrayOf(2, 3, 4)))
    }

    class SimpleDefinitionClass(
        @MinValue(0)
        val int: Int,
    )


    class DefinitionClass(
        @MinValue(1)
        val maxPlayers: Int,
        val aNumber: Int,
        @StringMinLength(5)
        val aString: String,
        @MaxValue(100)
        val aDouble: Double,
        val innerObj: DefinitionInnerObj,
    )

    class DefinitionInnerObj(
        @MinValue(20)
        val ssa: Int,
    )

    class MockDefinitionClass(
        val maxPlayers: String,
        val aNumber: Int,
        val aString: String,
        val aDouble: Double,
        val innerObj: MockDefinitionInnerObj,
    )

    class MockDefinitionInnerObj(
        val ssa: Int,
    )

    class DefinitionWithOptionalValue(
        val int: Int,
        @Optional
        val string: String?,
    )

    class DefinitionWithArray(
        val array: Array<Int>,
    )

}