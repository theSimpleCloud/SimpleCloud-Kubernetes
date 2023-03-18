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

import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceCustomAction
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceDefinition
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceDefinitionService
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.ResourceRequestHandler
import app.simplecloud.simplecloud.node.resourcedefinition.SchemaValidator
import eu.thesimplecloud.jsonlib.JsonLib
import org.yaml.snakeyaml.Yaml

/**
 * Date: 14.03.23
 * Time: 13:05
 * @author Frederick Baier
 *
 */
class WebRequestCustomActionHandler(
    private val group: String,
    private val version: String,
    private val kind: String,
    private val name: String,
    private val action: String,
    private val body: String,
    private val resourceDefinitionService: ResourceDefinitionService,
    private val resourceRequestHandler: ResourceRequestHandler,
) {

    private val resourceDefinition = getMatchingResourceDefinition()
    private val resourceVersion = resourceDefinition.getVersionByName(version)
    private val bodyAsJson = convertBodyToJson()

    fun handleCustomAction() {
        val actions = resourceVersion.getActions()
        val customAction = actions.getCustomActionByName(this.action)
        validateSchema(customAction)
        val body = bodyAsJson.getObject(customAction.getBodyClass())!!
        this.resourceRequestHandler.handleCustomAction(
            this.group,
            this.kind,
            this.version,
            this.name,
            this.action,
            body
        )
    }

    private fun validateSchema(customAction: ResourceCustomAction<*>) {
        val bodySchema = customAction.getBodySchema()
        SchemaValidator(bodySchema, this.bodyAsJson).validate()
    }

    private fun getMatchingResourceDefinition(): ResourceDefinition {
        return this.resourceDefinitionService.findResourceDefinition(this.group, this.kind)
    }

    private fun convertBodyToJson(): JsonLib {
        val map = Yaml().loadAs(this.body, Map::class.java)
        return JsonLib.fromObject(map)
    }

}