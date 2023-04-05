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

import app.simplecloud.simplecloud.api.resourcedefinition.Resource
import app.simplecloud.simplecloud.api.resourcedefinition.ResourceDto
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceDefinition
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersion
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.RequestSpecAndStatusResult
import app.simplecloud.simplecloud.node.resourcedefinition.RequestSpecAndStatusResultImpl
import eu.thesimplecloud.jsonlib.JsonLib

/**
 * Date: 04.02.23
 * Time: 10:10
 * @author Frederick Baier
 *
 */
class RequestUtil(
    private val resourceDefinition: ResourceDefinition,
    private val requestedVersion: ResourceVersion,
) {

    private val defaultVersion = this.resourceDefinition.getDefaultVersion()

    fun convertDefaultVersionToRequestVersion(resource: Resource): RequestSpecAndStatusResult<*, *> {
        val requestedVersionSpecObj = convertDefaultSpecToRequestedSpec(resource)
        return generateResourceDtoFromSpec(resource.name, requestedVersionSpecObj)
    }

    fun generateResourceDtoFromSpec(
        resourceName: String,
        requestedVersionSpecObj: Any,
    ): RequestSpecAndStatusResult<*, *> {
        val requestedVersionStatusObj = generateStatusForRequestedVersion(resourceName, requestedVersionSpecObj)
        val resourceDto = ResourceDto(
            this.resourceDefinition.getGroup() + "/" + this.requestedVersion.getName(),
            this.resourceDefinition.getKind(),
            resourceName,
            requestedVersionSpecObj,
            requestedVersionStatusObj
        )
        return RequestSpecAndStatusResultImpl<Any, Any>(resourceDto)
    }

    fun convertDefaultSpecToRequestedSpec(resource: Resource): Any {
        val defaultVersionSpecObj = JsonLib.fromObject(resource.spec).getObject(this.defaultVersion.getSpecClass())
        val converter = this.resourceDefinition.getVersionConverterFromVersionToDefaultVersion(this.requestedVersion)
        return converter.convertNewToOld(defaultVersionSpecObj)
    }

    private fun generateStatusForRequestedVersion(resourceName: String, specObj: Any): Any? {
        val statusGenerationFunction = this.requestedVersion.getStatusGenerationFunction()
        return statusGenerationFunction.generateStatus(resourceName, specObj)
    }

}