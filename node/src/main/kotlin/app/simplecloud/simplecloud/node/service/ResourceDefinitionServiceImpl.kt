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

package app.simplecloud.simplecloud.node.service

import app.simplecloud.simplecloud.module.api.impl.resourcedefinition.builder.ResourceDefinitionBuilderImpl
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceDefinition
import app.simplecloud.simplecloud.module.api.resourcedefinition.builder.ResourceDefinitionBuilder
import app.simplecloud.simplecloud.module.api.service.ResourceDefinitionService
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Date: 19.01.23
 * Time: 13:05
 * @author Frederick Baier
 *
 */
class ResourceDefinitionServiceImpl : ResourceDefinitionService {

    private val resourceDefinitions = CopyOnWriteArrayList<ResourceDefinition>()

    override fun findAll(): List<ResourceDefinition> {
        return this.resourceDefinitions
    }

    override fun findResourceDefinition(group: String, kind: String): ResourceDefinition {
        return this.resourceDefinitions.firstOrNull { it.getGroup() == group && it.getKind() == kind }
            ?: throw NoSuchElementException("ResourceDefinition does not exist")
    }

    override fun createResource(resourceDefinition: ResourceDefinition) {
        this.resourceDefinitions.add(resourceDefinition)
    }

    override fun newResourceDefinitionBuilder(): ResourceDefinitionBuilder {
        return ResourceDefinitionBuilderImpl()
    }


}