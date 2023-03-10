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

package app.simplecloud.simplecloud.node.resourcedefinition

import app.simplecloud.simplecloud.api.resourcedefinition.ResourceDto
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.RequestSpecAndStatusResult

/**
 * Date: 09.03.23
 * Time: 08:35
 * @author Frederick Baier
 *
 */
class RequestSpecAndStatusResultImpl<SPEC : Any, STATUS : Any>(
    private val resourceDto: ResourceDto,
) : RequestSpecAndStatusResult<SPEC, STATUS> {

    override fun getStatus(): STATUS? {
        return this.resourceDto.status as STATUS?
    }

    override fun getSpec(): SPEC {
        return this.resourceDto.spec as SPEC
    }

    override fun getGroup(): String {
        return this.resourceDto.apiVersion.split("/")[0]
    }

    override fun getVersion(): String {
        return this.resourceDto.apiVersion.split("/")[1]
    }

    override fun getKind(): String {
        return this.resourceDto.kind
    }

    override fun getName(): String {
        return this.resourceDto.name
    }

    fun getResourceDto(): ResourceDto {
        return this.resourceDto
    }

}