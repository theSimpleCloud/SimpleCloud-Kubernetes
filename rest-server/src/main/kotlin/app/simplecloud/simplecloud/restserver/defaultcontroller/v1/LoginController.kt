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

package app.simplecloud.simplecloud.restserver.defaultcontroller.v1

import app.simplecloud.simplecloud.restserver.annotation.RequestBody
import app.simplecloud.simplecloud.restserver.annotation.RequestMapping
import app.simplecloud.simplecloud.restserver.annotation.RestController
import app.simplecloud.simplecloud.restserver.auth.AuthServiceProvider
import app.simplecloud.simplecloud.restserver.base.route.RequestType
import app.simplecloud.simplecloud.restserver.base.service.UsernameAndPasswordCredentials
import app.simplecloud.simplecloud.restserver.controller.Controller
import com.google.inject.Inject
import kotlinx.coroutines.runBlocking

/**
 * Created by IntelliJ IDEA.
 * Date: 27.06.2021
 * Time: 17:59
 * @author Frederick Baier
 */
@RestController(1, "login")
class LoginController @Inject constructor(
    private val authServiceProvider: AuthServiceProvider
) : Controller {

    @RequestMapping(RequestType.POST, "", "")
    fun handleLogin(@RequestBody credentials: UsernameAndPasswordCredentials): TokenResponse = runBlocking {
        val authService = authServiceProvider.getAuthService()
        val token = authService.authenticate(credentials)
        return@runBlocking TokenResponse(token)
    }

    class TokenResponse(val token: String)


}