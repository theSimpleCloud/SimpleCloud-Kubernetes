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

import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceDefinition
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersion
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersionConverter

/**
 * Date: 19.01.23
 * Time: 13:11
 * @author Frederick Baier
 *
 */
interface ResourceDefinitionBuilder {

    fun setGroup(group: String): ResourceDefinitionBuilder

    fun setKind(kind: String): ResourceDefinitionBuilder

    fun addVersion(resourceVersion: ResourceVersion): ResourceDefinitionBuilder

    fun addVersionAsDefaultVersion(resourceVersion: ResourceVersion): ResourceDefinitionBuilder

    fun addVersionConverter(resourceVersionConverter: ResourceVersionConverter<*, *>): ResourceDefinitionBuilder

    fun build(): ResourceDefinition

    fun newResourceVersionBuilder(): ResourceVersionBuilder

}