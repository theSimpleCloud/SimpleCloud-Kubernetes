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

import app.simplecloud.simplecloud.database.api.DatabaseResourceRepository
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.RequestResult
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.RequestSpecAndStatusResult
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.RequestSpecResult
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.ResourceRequestHandler
import app.simplecloud.simplecloud.module.api.service.ResourceDefinitionService

/**
 * Date: 08.03.23
 * Time: 11:07
 * @author Frederick Baier
 *
 */
class ResourceRequestHandlerImpl(
    private val databaseResourceRepository: DatabaseResourceRepository,
    private val resourceDefinitionService: ResourceDefinitionService,
) : ResourceRequestHandler {

    override fun <SPEC : Any, STATUS : Any> handleGetAllSpecAndStatus(
        group: String,
        kind: String,
        version: String,
    ): List<RequestSpecAndStatusResult<SPEC, STATUS>> {
        return RequestGetAllHandler(
            group,
            version,
            kind,
            this.resourceDefinitionService,
            this.databaseResourceRepository
        ).handleGetAll() as List<RequestSpecAndStatusResult<SPEC, STATUS>>
    }

    override fun <S : Any> handleGetAllSpec(group: String, kind: String, version: String): List<RequestSpecResult<S>> {
        return handleGetAllSpecAndStatus<Any, Any>(group, kind, version) as List<RequestSpecResult<S>>
    }

    override fun handleGetAll(group: String, kind: String, version: String): List<RequestResult> {
        return handleGetAllSpec<Any>(group, kind, version)
    }

    override fun <SPEC : Any, STATUS : Any> handleGetOneSpecAndStatus(
        group: String,
        kind: String,
        version: String,
        fieldName: String,
        fieldValue: String,
    ): RequestSpecAndStatusResult<SPEC, STATUS> {
        return RequestGetOneHandler(
            group,
            version,
            kind,
            fieldName,
            fieldValue,
            this.resourceDefinitionService,
            this.databaseResourceRepository
        ).handleGetOne() as RequestSpecAndStatusResult<SPEC, STATUS>
    }

    override fun <SPEC : Any, STATUS : Any> handleGetOneSpecAndStatus(
        group: String,
        kind: String,
        version: String,
        name: String,
    ): RequestSpecAndStatusResult<SPEC, STATUS> {
        if (!name.contains(":")) {
            return handleGetOneSpecAndStatus(group, kind, version, "name", name)
        }
        val (innerFieldName, innerFieldValue) = name.split(":")
        return handleGetOneSpecAndStatus(group, kind, version, "spec.$innerFieldName", innerFieldValue)
    }

    override fun <S : Any> handleGetOneSpec(
        group: String,
        kind: String,
        version: String,
        name: String,
    ): RequestSpecResult<S> {
        return handleGetOneSpecAndStatus<Any, Any>(group, kind, version, name) as RequestSpecResult<S>
    }

    override fun handleGetOne(group: String, kind: String, version: String, name: String): RequestResult {
        return handleGetOneSpec<Any>(group, kind, version, name)
    }

    override fun handleCreate(group: String, kind: String, version: String, name: String, spec: Any) {
        RequestCreateAndUpdateHandler(
            group,
            kind,
            version,
            name,
            spec,
            this.resourceDefinitionService,
            this.databaseResourceRepository
        ).handleCreate()
    }

    override fun handleUpdate(group: String, kind: String, version: String, name: String, spec: Any) {
        RequestCreateAndUpdateHandler(
            group,
            kind,
            version,
            name,
            spec,
            this.resourceDefinitionService,
            this.databaseResourceRepository
        ).handleUpdate()
    }

    override fun handleDelete(group: String, kind: String, version: String, name: String) {
        RequestDeleteOneHandler(
            group,
            version,
            kind,
            name,
            resourceDefinitionService,
            databaseResourceRepository
        ).handleDeleteOne()
    }

    override fun handleCustomAction(
        group: String,
        kind: String,
        version: String,
        name: String,
        action: String,
        body: Any,
    ) {
        RequestCustomActionHandler(
            group,
            kind,
            version,
            name,
            action,
            body,
            this.resourceDefinitionService,
            this
        ).handleCustomAction()
    }
}