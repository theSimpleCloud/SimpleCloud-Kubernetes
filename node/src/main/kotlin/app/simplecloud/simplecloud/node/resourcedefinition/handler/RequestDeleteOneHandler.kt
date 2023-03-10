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
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceDefinition
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceDefinitionService
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersion
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersionRequestPreProcessor

class RequestDeleteOneHandler(
    private val group: String,
    private val version: String,
    private val kind: String,
    private val name: String,
    private val resourceDefinitionService: ResourceDefinitionService,
    private val databaseResourceRepository: DatabaseResourceRepository,
) {

    private val resourceDefinition = getMatchingResourceDefinition()
    private val defaultVersion = resourceDefinition.getDefaultVersion()
    private val requestedVersion = getResourceVersion()

    fun handleDeleteOne(): Any {
        checkGroupNotInternal()
        if (this.requestedVersion.getActions().isDeleteDisabled()) {
            throw IllegalStateException("Delete Requests are disabled")
        }

        handleDeletePreProcessor()
        val apiVersion = this.group + "/" + this.defaultVersion.getName()
        if (this.databaseResourceRepository.load(apiVersion, this.kind, this.name) == null) {
            throw NoSuchElementException("Resource not found")
        }
        this.databaseResourceRepository.delete(apiVersion, this.kind, this.name)
        return true
    }

    private fun checkGroupNotInternal() {
        if (this.group == "internal")
            throw IllegalArgumentException("Cannot create internal resource")
    }

    private fun handleDeletePreProcessor() {
        val preProcessorResult = callDeletePreProcessor()
        when (preProcessorResult) {
            is ResourceVersionRequestPreProcessor.ContinueResult -> {}
            is ResourceVersionRequestPreProcessor.UnsupportedRequest -> throw RequestGetOneHandler.UnsupportedRequestException()
        }
    }

    private fun callDeletePreProcessor(): ResourceVersionRequestPreProcessor.RequestPreProcessorResult<Any> {
        val preProcessor = this.requestedVersion.getPreProcessor()
        return preProcessor.processDelete(this.group, this.requestedVersion.getName(), this.kind, this.name)
    }


    private fun getMatchingResourceDefinition(): ResourceDefinition {
        return this.resourceDefinitionService.findResourceDefinition(this.group, this.kind)
    }

    private fun getResourceVersion(): ResourceVersion {
        return resourceDefinition.getVersionByName(this.version)
    }

}
