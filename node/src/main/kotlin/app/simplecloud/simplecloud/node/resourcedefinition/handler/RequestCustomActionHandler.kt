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

package app.simplecloud.simplecloud.node.resourcedefinition.handler

import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceCustomAction
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceCustomActionHandler
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceDefinition
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersion
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.RequestResult
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.ResourceRequestHandler
import app.simplecloud.simplecloud.module.api.service.ResourceDefinitionService

class RequestCustomActionHandler(
    private val group: String,
    private val kind: String,
    private val version: String,
    private val name: String,
    private val action: String,
    private val body: Any,
    private val resourceDefinitionService: ResourceDefinitionService,
    private val requestHandler: ResourceRequestHandler,
) {

    private val resourceDefinition = getMatchingResourceDefinition()
    private val requestedVersion = getRequestedVersion()

    fun handleCustomAction() {
        val requestResult = loadRequestedResource()
        val resourceVersionActions = this.requestedVersion.getActions()
        val customAction = resourceVersionActions.getCustomActionByName(this.action)
        checkBodyType(customAction)
        val actionHandler = customAction.getResourceCustomActionHandler() as ResourceCustomActionHandler<Any>
        actionHandler.handleAction(requestResult.getName(), body)
    }

    private fun checkBodyType(customAction: ResourceCustomAction<*>) {
        if (customAction.getBodyClass() != this.body::class.java) {
            throw IllegalArgumentException("Invalid body class: Expected '${customAction.getBodyClass().name}' but got '${this.body::class.java.name}'")
        }
    }

    private fun loadRequestedResource(): RequestResult {
        return this.requestHandler.handleGetOne(this.group, this.kind, this.version, this.name)
    }

    private fun getRequestedVersion(): ResourceVersion {
        return this.resourceDefinition.getVersionByName(this.version)
    }

    private fun getMatchingResourceDefinition(): ResourceDefinition {
        return this.resourceDefinitionService.findResourceDefinition(this.group, this.kind)
    }

}
