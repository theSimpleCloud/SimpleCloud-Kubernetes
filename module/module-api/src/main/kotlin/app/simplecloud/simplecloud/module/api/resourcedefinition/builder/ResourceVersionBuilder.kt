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

package app.simplecloud.simplecloud.module.api.resourcedefinition.builder

import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersion
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersionActions
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersionRequestPreProcessor
import app.simplecloud.simplecloud.module.api.resourcedefinition.StatusGenerationFunction

/**
 * Date: 19.01.23
 * Time: 13:12
 * @author Frederick Baier
 *
 */
interface ResourceVersionBuilder {

    fun setName(name: String): ResourceVersionBuilder

    fun setSpecSchemaClass(specClass: Class<*>): ResourceVersionBuilder

    fun setStatusSchemaClass(statusClass: Class<*>): ResourceVersionBuilder

    fun setStatusGenerationFunction(function: StatusGenerationFunction<*>): ResourceVersionBuilder

    fun setPreProcessor(preProcessor: ResourceVersionRequestPreProcessor<*>): ResourceVersionBuilder

    fun setActions(actions: ResourceVersionActions): ResourceVersionBuilder

    fun newActionsBuilder(): ResourceVersionActionsBuilder

    fun build(): ResourceVersion

}