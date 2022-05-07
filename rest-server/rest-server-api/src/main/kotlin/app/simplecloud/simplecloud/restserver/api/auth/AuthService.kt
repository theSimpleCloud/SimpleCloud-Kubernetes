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

package app.simplecloud.simplecloud.restserver.api.auth

import app.simplecloud.simplecloud.api.permission.PermissionEntity

/**
 * Created by IntelliJ IDEA.
 * Date: 23.06.2021
 * Time: 14:50
 * @author Frederick Baier
 */
interface AuthService {

    /**
     * Returns the jwt token for the specified credentials
     */
    suspend fun authenticate(usernameAndPasswordCredentials: UsernameAndPasswordCredentials): String

    /**
     * Returns the user from the specified [headers]
     */
    suspend fun getRequestEntityFromHeader(headers: Headers): PermissionEntity

}