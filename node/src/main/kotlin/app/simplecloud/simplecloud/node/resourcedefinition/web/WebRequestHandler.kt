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

package app.simplecloud.simplecloud.node.resourcedefinition.web

import app.simplecloud.simplecloud.api.resourcedefinition.ResourceDto
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceDefinitionService
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.RequestResult
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.ResourceRequestHandler
import app.simplecloud.simplecloud.node.resourcedefinition.web.handler.InternalRequestGetHandler
import app.simplecloud.simplecloud.node.resourcedefinition.web.handler.WebRequestCreateAndUpdateHandler

/**
 * Date: 15.01.23
 * Time: 12:45
 * @author Frederick Baier
 *
 */
class WebRequestHandler(
    private val resourceDefinitionService: ResourceDefinitionService,
    private val resourceRequestHandler: ResourceRequestHandler,
) {

    fun handleCreate(body: String): Boolean {
        WebRequestCreateAndUpdateHandler(body, resourceDefinitionService, resourceRequestHandler).handleCreate()
        return true
    }

    fun handleUpdate(body: String): Boolean {
        WebRequestCreateAndUpdateHandler(body, resourceDefinitionService, resourceRequestHandler).handleUpdate()
        return true
    }

    fun handleGetOne(group: String, version: String, kind: String, name: String): ResourceDto {
        if (group == "internal") {
            return InternalRequestGetHandler(group, version, kind, name, resourceDefinitionService).handleGetOne()
        }
        val requestResult = this.resourceRequestHandler.handleGetOne(group, kind, version, name)
        return convertRequestResultToResourceDto(requestResult)
    }

    fun handleGetAll(group: String, version: String, kind: String): List<ResourceDto> {
        if (group == "internal") {
            return InternalRequestGetHandler(group, version, kind, resourceDefinitionService).handleGetAll()
        }
        val list = this.resourceRequestHandler.handleGetAll(group, kind, version)
        return list.map { convertRequestResultToResourceDto(it) }
    }

    fun handleDeleteOne(group: String, version: String, kind: String, name: String): Boolean {
        this.resourceRequestHandler.handleDelete(group, kind, version, name)
        return true
    }

    private fun convertRequestResultToResourceDto(requestResult: RequestResult): ResourceDto {
        return ResourceDto(
            requestResult.getGroup() + "/" + requestResult.getVersion(),
            requestResult.getKind(),
            requestResult.getName(),
            requestResult.getSpec(),
            requestResult.getStatus()
        )
    }

}