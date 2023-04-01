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
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersion
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersionRequestPrePostProcessor
import app.simplecloud.simplecloud.module.api.service.ResourceDefinitionService
import app.simplecloud.simplecloud.node.resourcedefinition.web.handler.RequestUtil

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

    private val requestUtil = RequestUtil(this.resourceDefinition, this.requestedVersion)

    fun handleDeleteOne(): Any {
        checkGroupNotInternal()
        if (this.requestedVersion.getActions().isDeleteDisabled()) {
            throw IllegalStateException("Delete Requests are disabled")
        }

        val continueWithResult = handleDeletePreProcessor()
        if (!continueWithResult)
            return true

        val apiVersion = this.group + "/" + this.defaultVersion.getName()
        val resource = this.databaseResourceRepository.load(apiVersion, this.kind, "name", this.name)
            ?: throw NoSuchElementException("Resource not found")
        val requestedSpec = this.requestUtil.convertDefaultSpecToRequestedSpec(resource)
        this.databaseResourceRepository.delete(apiVersion, this.kind, this.name)
        handlePostDelete(requestedSpec)
        return true
    }

    private fun handlePostDelete(requestedSpec: Any) {
        val preProcessor = this.requestedVersion.getPreProcessor()
        preProcessor.postDelete(this.group, this.version, this.kind, this.name, requestedSpec)
    }

    private fun checkGroupNotInternal() {
        if (this.group == "internal")
            throw IllegalArgumentException("Cannot create internal resource")
    }

    private fun handleDeletePreProcessor(): Boolean {
        val preProcessorResult = callDeletePreProcessor()
        when (preProcessorResult) {
            is ResourceVersionRequestPrePostProcessor.ContinueResult -> {}
            is ResourceVersionRequestPrePostProcessor.UnsupportedRequest -> throw RequestGetOneHandler.UnsupportedRequestException()
            is ResourceVersionRequestPrePostProcessor.BlockResult -> return false
            is ResourceVersionRequestPrePostProcessor.OverwriteSpec -> {}
        }
        return true
    }

    private fun callDeletePreProcessor(): ResourceVersionRequestPrePostProcessor.RequestPreProcessorResult<Any> {
        val preProcessor = this.requestedVersion.getPreProcessor()
        return preProcessor.preDelete(this.group, this.requestedVersion.getName(), this.kind, this.name)
    }


    private fun getMatchingResourceDefinition(): ResourceDefinition {
        return this.resourceDefinitionService.findResourceDefinition(this.group, this.kind)
    }

    private fun getResourceVersion(): ResourceVersion {
        return resourceDefinition.getVersionByName(this.version)
    }

}
