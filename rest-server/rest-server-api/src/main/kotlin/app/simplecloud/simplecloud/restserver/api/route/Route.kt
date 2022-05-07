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

package app.simplecloud.simplecloud.restserver.api.route

import app.simplecloud.simplecloud.restserver.api.Request

/**
 * Date: 14.03.22
 * Time: 11:15
 * @author Frederick Baier
 *
 */
interface Route {

    fun getRequestType(): RequestType

    fun getPath(): String

    /**
     * Returns the permission or an empty string if no permission was set
     */
    fun getPermission(): String

    fun hasPermission(): Boolean {
        return getPermission().isNotEmpty()
    }

    fun handleRequest(request: Request): Any?

}