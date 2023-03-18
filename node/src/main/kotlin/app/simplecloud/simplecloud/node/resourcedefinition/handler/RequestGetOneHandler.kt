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

import app.simplecloud.simplecloud.api.resourcedefinition.Resource
import app.simplecloud.simplecloud.database.api.DatabaseResourceRepository
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceDefinitionService
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersionRequestPreProcessor
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.RequestSpecAndStatusResult
import app.simplecloud.simplecloud.node.resourcedefinition.web.handler.RequestGetUtil

/**
 * Date: 04.02.23
 * Time: 10:07
 * @author Frederick Baier
 *
 */
class RequestGetOneHandler(
    private val group: String,
    private val version: String,
    private val kind: String,
    private val fieldName: String,
    private val fieldValue: String,
    private val resourceDefinitionService: ResourceDefinitionService,
    private val databaseResourceRepository: DatabaseResourceRepository,
) {

    private val resourceDefinition = this.resourceDefinitionService.findResourceDefinition(this.group, this.kind)
    private val defaultVersion = this.resourceDefinition.getDefaultVersion()
    private val requestedVersion = this.resourceDefinition.getVersionByName(this.version)

    private val requestGetUtil = RequestGetUtil(this.resourceDefinition, this.requestedVersion)

    fun handleGetOne(): RequestSpecAndStatusResult<*, *> {
        val preProcessorResult = handlePreProcessor()
        when (preProcessorResult) {
            is ResourceVersionRequestPreProcessor.ContinueResult -> {}
            is ResourceVersionRequestPreProcessor.UnsupportedRequest -> throw UnsupportedRequestException()
            is ResourceVersionRequestPreProcessor.BlockResult -> {}
            is ResourceVersionRequestPreProcessor.OverwriteSpec -> {}
        }
        val resource = loadResource() ?: throw NoSuchElementException("Resource not found")
        val requestedSpec = requestGetUtil.convertDefaultSpecToRequestedSpec(resource)
        return requestGetUtil.generateResourceDtoFromSpec(resource, requestedSpec)
    }

    private fun handlePreProcessor(): ResourceVersionRequestPreProcessor.RequestPreProcessorResult<Any> {
        val preProcessor = this.requestedVersion.getPreProcessor()
        return preProcessor.processGetOne(this.group, this.version, this.kind, this.fieldName, this.fieldValue)
    }

    private fun loadResource(): Resource? {
        return this.databaseResourceRepository.load(
            "${this.group}/${this.defaultVersion.getName()}",
            this.kind,
            this.fieldName,
            this.fieldValue
        )
    }

    class UnsupportedRequestException() : Exception("Request not supported")

}