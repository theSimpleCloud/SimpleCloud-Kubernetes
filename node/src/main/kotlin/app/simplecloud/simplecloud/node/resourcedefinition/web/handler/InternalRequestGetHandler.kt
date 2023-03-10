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

package app.simplecloud.simplecloud.node.resourcedefinition.web.handler

import app.simplecloud.simplecloud.api.resourcedefinition.ResourceDto
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceDefinition
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceDefinitionService
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersion
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersionActions
import eu.thesimplecloud.jsonlib.JsonLib

/**
 * Date: 06.03.23
 * Time: 15:13
 * @author Frederick Baier
 *
 */
class InternalRequestGetHandler(
    private val group: String,
    private val version: String,
    private val kind: String,
    private val name: String,
    private val resourceDefinitionService: ResourceDefinitionService,
) {

    constructor(
        group: String,
        version: String,
        kind: String,
        resourceDefinitionService: ResourceDefinitionService,
    ) : this(group, version, kind, "", resourceDefinitionService)

    init {
        if (this.kind != "ResourceDefinition")
            throw IllegalArgumentException("Unknown kind: ${this.kind}")
    }

    fun handleGetOne(): ResourceDto {
        val split = name.split("+")
        val requestedGroup = split[0]
        val requestedKind = split[1]
        val resourceDefinition = this.resourceDefinitionService.findResourceDefinition(requestedGroup, requestedKind)
        return createResourceForResourceDefinition(resourceDefinition)
    }

    private fun createResourceForResourceDefinition(resourceDefinition: ResourceDefinition): ResourceDto {
        val json = convertResourceDefinitionToJson(resourceDefinition)
        val specMap = json.getObject(Map::class.java)
        return ResourceDto(
            "${this.group}/${this.version}",
            this.kind,
            resourceDefinition.getGroup() + "+" + resourceDefinition.getKind(),
            specMap,
            null
        )
    }

    private fun convertResourceDefinitionToJson(resourceDefinition: ResourceDefinition): JsonLib {
        val versions = resourceDefinition.getVersions()
        val versionJsons = versions.map { convertResourceVersionToJson(it) }
        return JsonLib.empty()
            .append("versions", versionJsons)
            .append("defaultVersion", resourceDefinition.getDefaultVersion().getName())
    }

    private fun convertResourceVersionToJson(resourceVersion: ResourceVersion): JsonLib {
        return JsonLib.empty()
            .append("name", resourceVersion.getName())
            .append("specSchema", resourceVersion.getSpecSchema())
            .append("statusSchema", resourceVersion.getStatusClass())
            .append("actions", convertActionsToJson(resourceVersion.getActions()))
    }

    private fun convertActionsToJson(actions: ResourceVersionActions): JsonLib {
        return JsonLib.empty()
            .append("isCreateDisabled", actions.isCreateDisabled())
            .append("isUpdateDisabled", actions.isUpdateDisabled())
            .append("isDeleteDisabled", actions.isDeleteDisabled())
            .append("createActionName", actions.getCreateActionName())
            .append("updateActionName", actions.getUpdateActionName())
            .append("deleteActionName", actions.getDeleteActionName())
    }

    fun handleGetAll(): List<ResourceDto> {
        val resourceDefinitions = this.resourceDefinitionService.findAll()
        return resourceDefinitions.map { createResourceForResourceDefinition(it) }
    }

}