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

import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceDefinition
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersion
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.ResourceRequestHandler
import app.simplecloud.simplecloud.module.api.service.ResourceDefinitionService
import app.simplecloud.simplecloud.node.resourcedefinition.MissingResourcePropertyException
import app.simplecloud.simplecloud.node.resourcedefinition.SchemaValidator
import eu.thesimplecloud.jsonlib.JsonLib
import org.yaml.snakeyaml.Yaml

/**
 * Date: 21.01.23
 * Time: 22:17
 * @author Frederick Baier
 *
 */
class WebRequestCreateAndUpdateHandler(
    private val body: String,
    private val resourceDefinitionService: ResourceDefinitionService,
    private val resourceRequestHandler: ResourceRequestHandler,
) {
    private val bodyJson = convertBodyToJson()

    private val apiVersion = getApiVersionFromBody()
    private val group = this.apiVersion.split("/")[0]
    private val kind = getKindFromBody()
    private val name = getNameFromBody()

    private val resourceDefinition = getMatchingResourceDefinition()
    private val requestedVersion = getResourceVersion()

    fun handleCreate() {
        val specObj = getSpecObjFromBody(requestedVersion)
        this.resourceRequestHandler.handleCreate(group, kind, requestedVersion.getName(), name, specObj)
    }

    fun handleUpdate() {
        val specObj = getSpecObjFromBody(requestedVersion)
        this.resourceRequestHandler.handleUpdate(group, kind, requestedVersion.getName(), name, specObj)
    }

    private fun getSpecObjFromBody(resourceVersion: ResourceVersion): Any {
        val specJson = getSpecJsonFromBody()
        SchemaValidator(resourceVersion.getSpecSchema(), specJson).validate()
        return specJson.getObject(resourceVersion.getSpecClass())
    }

    private fun getSpecJsonFromBody(): JsonLib {
        return this.bodyJson.getProperty("spec") ?: throw NoSuchElementException("Missing property (root).spec")
    }

    private fun getResourceVersion(): ResourceVersion {
        val versionName = apiVersion.split("/")[1]
        return resourceDefinition.getVersionByName(versionName)
    }

    private fun getApiVersionFromBody(): String {
        return this.bodyJson.getString("apiVersion") ?: throw MissingResourcePropertyException("apiVersion")
    }

    private fun getKindFromBody(): String {
        return this.bodyJson.getString("kind") ?: throw MissingResourcePropertyException("kind")
    }

    private fun getNameFromBody(): String {
        return this.bodyJson.getString("name") ?: throw MissingResourcePropertyException("name")
    }

    private fun getMatchingResourceDefinition(): ResourceDefinition {
        return this.resourceDefinitionService.findResourceDefinition(this.group, this.kind)
    }

    private fun convertBodyToJson(): JsonLib {
        val map = Yaml().loadAs(this.body, Map::class.java)
        return JsonLib.fromObject(map)
    }


}

