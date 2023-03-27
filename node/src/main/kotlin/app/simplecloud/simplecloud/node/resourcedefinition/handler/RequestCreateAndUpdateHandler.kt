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
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceDefinition
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceDefinitionService
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersion
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersionRequestPrePostProcessor
import eu.thesimplecloud.jsonlib.JsonLib

/**
 * Date: 21.01.23
 * Time: 22:17
 * @author Frederick Baier
 *
 */
class RequestCreateAndUpdateHandler(
    private val group: String,
    private val kind: String,
    private val version: String,
    private val name: String,
    private val specObj: Any,
    private val resourceDefinitionService: ResourceDefinitionService,
    private val databaseResourceRepository: DatabaseResourceRepository,
) {

    private val resourceDefinition = getMatchingResourceDefinition()
    private val defaultVersion = resourceDefinition.getDefaultVersion()
    private val requestedVersion = getResourceVersion()

    fun handleCreate() {
        checkGroupNotInternal()
        if (this.requestedVersion.getActions().isCreateDisabled()) {
            throw IllegalStateException("Create Requests are disabled")
        }
        checkResourceAlreadyExists()
        val resourceToSave = createCreateResourceToSave()
        this.databaseResourceRepository.save(resourceToSave)
        handlePostCreate()
    }

    private fun loadCurrentResourceFromDefaultVersion(): Resource? {
        return this.databaseResourceRepository.load(
            "${this.group}/${this.defaultVersion.getName()}",
            this.kind,
            "name",
            this.name
        )
    }

    private fun checkResourceAlreadyExists() {
        if (loadCurrentResourceFromDefaultVersion() != null) {
            throw IllegalStateException("Resource already exists")
        }
    }

    fun handleUpdate() {
        checkGroupNotInternal()
        if (this.requestedVersion.getActions().isUpdateDisabled()) {
            throw IllegalStateException("Update Requests are disabled")
        }
        checkResourceNotExists()
        val resourceToSave = createUpdateResourceToSave()
        this.databaseResourceRepository.update(resourceToSave)
        handlePostUpdate()
    }

    private fun checkResourceNotExists() {
        if (loadCurrentResourceFromDefaultVersion() == null) {
            throw IllegalStateException("Resource does not exist")
        }
    }

    private fun checkGroupNotInternal() {
        if (this.group == "internal")
            throw IllegalArgumentException("Cannot create internal resource")
    }

    private fun handlePostCreate() {
        val preProcessor = getPreProcessor<Any>()
        preProcessor.postCreate(this.group, this.version, this.kind, this.name, this.specObj)
    }

    private fun handlePostUpdate() {
        val preProcessor = getPreProcessor<Any>()
        preProcessor.postUpdate(this.group, this.version, this.kind, this.name, this.specObj)
    }

    private fun <S> getPreProcessor(): ResourceVersionRequestPrePostProcessor<S> {
        return this.requestedVersion.getPreProcessor() as ResourceVersionRequestPrePostProcessor<S>
    }

    private fun <S> handleUpdatePreProcessor(specObj: S): ResourceVersionRequestPrePostProcessor.RequestPreProcessorResult<S> {
        val preProcessor = getPreProcessor<S>()
        return preProcessor.preUpdate(this.group, this.requestedVersion.getName(), this.kind, this.name, specObj)
    }

    private fun <S> handleCreatePreProcessor(specObj: S): ResourceVersionRequestPrePostProcessor.RequestPreProcessorResult<S> {
        val preProcessor = getPreProcessor<S>()
        return preProcessor.preCreate(this.group, this.requestedVersion.getName(), this.kind, this.name, specObj)
    }

    private fun createUpdateResourceToSave(): Resource {
        val preProcessorResult = handleUpdatePreProcessor(this.specObj)
        when (preProcessorResult) {
            is ResourceVersionRequestPrePostProcessor.ContinueResult -> {}
            is ResourceVersionRequestPrePostProcessor.UnsupportedRequest -> throw RequestGetOneHandler.UnsupportedRequestException()
            is ResourceVersionRequestPrePostProcessor.BlockResult -> {}
            is ResourceVersionRequestPrePostProcessor.OverwriteSpec -> {
                return createResourceFromRequestedSpecObj(preProcessorResult.spec)
            }
        }
        return createResourceFromRequestedSpecObj(this.specObj)
    }

    private fun createCreateResourceToSave(): Resource {
        val preProcessorResult = handleCreatePreProcessor(this.specObj)
        when (preProcessorResult) {
            is ResourceVersionRequestPrePostProcessor.ContinueResult -> {}
            is ResourceVersionRequestPrePostProcessor.UnsupportedRequest -> throw RequestGetOneHandler.UnsupportedRequestException()
            is ResourceVersionRequestPrePostProcessor.BlockResult -> {}
            is ResourceVersionRequestPrePostProcessor.OverwriteSpec -> {
                return createResourceFromRequestedSpecObj(preProcessorResult.spec)
            }
        }
        return createResourceFromRequestedSpecObj(specObj)
    }

    private fun createResourceFromRequestedSpecObj(specObj: Any): Resource {
        val convertedSpecObj = convertSpecObjToDefaultVersion(specObj, this.requestedVersion)
        return createResourceObjToSave(convertedSpecObj)
    }


    private fun convertSpecObjToDefaultVersion(specObj: Any, resourceVersion: ResourceVersion): Any {
        val converter = this.resourceDefinition.getVersionConverterFromVersionToDefaultVersion(resourceVersion)
        return converter.convertOldToNew(specObj)
    }

    private fun getResourceVersion(): ResourceVersion {
        return resourceDefinition.getVersionByName(this.version)
    }

    private fun createResourceObjToSave(specObj: Any): Resource {
        return JsonLib.empty()
            .append("apiVersion", this.group + "/" + defaultVersion.getName())
            .append("kind", kind)
            .append("name", name)
            .append("spec", JsonLib.fromObject(specObj))
            .getObject(Resource::class.java)
    }

    private fun getMatchingResourceDefinition(): ResourceDefinition {
        return this.resourceDefinitionService.findResourceDefinition(this.group, this.kind)
    }

}

