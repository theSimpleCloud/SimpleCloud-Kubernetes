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

package app.simplecloud.simplecloud.module.api.impl.resourcedefinition.builder

import app.simplecloud.simplecloud.module.api.impl.resourcedefinition.ResourceVersionImpl
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersion
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersionActions
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersionRequestPrePostProcessor
import app.simplecloud.simplecloud.module.api.resourcedefinition.StatusGenerationFunction
import app.simplecloud.simplecloud.module.api.resourcedefinition.builder.ResourceVersionActionsBuilder
import app.simplecloud.simplecloud.module.api.resourcedefinition.builder.ResourceVersionBuilder

/**
 * Date: 19.01.23
 * Time: 13:40
 * @author Frederick Baier
 *
 */
class ResourceVersionBuilderImpl : ResourceVersionBuilder {

    @Volatile
    private var name: String? = null

    @Volatile
    private var specClass: Class<*>? = null

    @Volatile
    private var statusClass: Class<*>? = null

    @Volatile
    private var statusGenerationFunction: StatusGenerationFunction<*>? = null

    @Volatile
    private var actions: ResourceVersionActions = newActionsBuilder().build()

    @Volatile
    private var preProcessor: ResourceVersionRequestPrePostProcessor<*> = ResourceVersionRequestPrePostProcessor<Any>()

    override fun setName(name: String): ResourceVersionBuilder {
        this.name = name
        return this
    }

    override fun setSpecSchemaClass(specClass: Class<*>): ResourceVersionBuilder {
        this.specClass = specClass
        return this
    }

    override fun setStatusSchemaClass(statusClass: Class<*>): ResourceVersionBuilder {
        this.statusClass = statusClass
        return this
    }

    override fun setStatusGenerationFunction(function: StatusGenerationFunction<*>): ResourceVersionBuilder {
        this.statusGenerationFunction = function
        return this
    }

    override fun setPreProcessor(preProcessor: ResourceVersionRequestPrePostProcessor<*>): ResourceVersionBuilder {
        this.preProcessor = preProcessor
        return this
    }

    override fun setActions(actions: ResourceVersionActions): ResourceVersionBuilder {
        this.actions = actions
        return this
    }

    override fun newActionsBuilder(): ResourceVersionActionsBuilder {
        return ResourceVersionActionBuilderImpl()
    }


    override fun build(): ResourceVersion {
        if (this.name == null)
            throw IllegalArgumentException("Name must not be null")
        if (this.specClass == null)
            throw IllegalArgumentException("Status class must not be null")

        return ResourceVersionImpl(
            this.name!!,
            this.specClass!!,
            this.statusClass,
            this.actions,
            this.statusGenerationFunction,
            this.preProcessor
        )
    }
}