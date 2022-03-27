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

package app.simplecloud.simplecloud.restserver.base.request

import app.simplecloud.simplecloud.api.permission.PermissionEntity
import app.simplecloud.simplecloud.restserver.base.route.RequestType

/**
 * Date: 14.03.22
 * Time: 11:37
 * @author Frederick Baier
 *
 */
class RequestImpl(
    private val requestType: RequestType,
    private val requestPath: String,
    private val requestBody: String,
    private val entity: PermissionEntity?,
    private val pathParameters: Map<String, String>
) : Request {
    override fun getRequestType(): RequestType {
        return this.requestType
    }

    override fun getRequestPath(): String {
        return this.requestPath
    }

    override fun getRequestingEntity(): PermissionEntity? {
        return this.entity
    }

    override fun getRequestBody(): String {
        return this.requestBody
    }

    override fun getPathParameter(name: String): String {
        return this.pathParameters[name] ?: throw NoSuchElementException("No Parameter for '${name}' was provided")
    }
}