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

package app.simplecloud.simplecloud.module.api.impl.resourcedefinition

import app.simplecloud.simplecloud.api.resourcedefinition.Definition
import app.simplecloud.simplecloud.module.api.impl.resourcedefinition.schema.SchemaCreator
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersion
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersionActions
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersionRequestPreProcessor
import app.simplecloud.simplecloud.module.api.resourcedefinition.StatusGenerationFunction

/**
 * Date: 19.01.23
 * Time: 13:48
 * @author Frederick Baier
 *
 */
class ResourceVersionImpl(
    private val name: String,
    private val specClass: Class<*>,
    private val statusClass: Class<*>?,
    private val actions: ResourceVersionActions,
    private val statusGenerationFunction: StatusGenerationFunction<*>?,
    private val preProcessor: ResourceVersionRequestPreProcessor<*>,
) : ResourceVersion {

    private val specSchema = SchemaCreator(specClass, false, emptyList()).createSchema()
    private val statusSchema =
        if (statusClass == null) null else SchemaCreator(statusClass, false, emptyList()).createSchema()

    override fun getName(): String {
        return this.name
    }

    override fun getSpecClass(): Class<*> {
        return this.specClass
    }

    override fun getStatusClass(): Class<*>? {
        return this.statusClass
    }

    override fun getStatusGenerationFunction(): StatusGenerationFunction<Any> {
        return (this.statusGenerationFunction as StatusGenerationFunction<Any>?)
            ?: StatusGenerationFunction<Any> { _, _ -> null }
    }

    override fun getPreProcessor(): ResourceVersionRequestPreProcessor<Any> {
        return this.preProcessor as ResourceVersionRequestPreProcessor<Any>
    }

    override fun getSpecSchema(): Definition {
        return this.specSchema
    }

    override fun getStatusSchema(): Definition? {
        return this.statusSchema
    }

    override fun getActions(): ResourceVersionActions {
        return this.actions
    }

}