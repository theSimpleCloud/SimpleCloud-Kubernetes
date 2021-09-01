/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.simplecloud.restserver.defaultcontroller.v1

import com.ea.async.Async.await
import com.google.inject.Inject
import eu.thesimplecloud.simplecloud.restserver.annotation.*
import eu.thesimplecloud.simplecloud.restserver.controller.IController
import eu.thesimplecloud.simplecloud.restserver.service.IUserService
import eu.thesimplecloud.simplecloud.restserver.user.User

/**
 * Created by IntelliJ IDEA.
 * Date: 27.06.2021
 * Time: 12:09
 * @author Frederick Baier
 */
@Controller(1, "user")
class UserController @Inject constructor(
    private val userService: IUserService
) : IController {

    @RequestMapping(RequestType.GET, "self", "web.user.get.self")
    fun handleUserGetSelf(@RequestingUser user: User): User {
        return user
    }

    @RequestMapping(RequestType.GET, "{name}", "web.user.get.one")
    fun handleGetByName(@RequestPathParam("name") requestingUserName: String): User {
        return await(userService.getUserByName(requestingUserName))
    }

    @RequestMapping(RequestType.POST, "", "web.user.create")
    fun handleUserCreate(@RequestBody user: User): Boolean {
        userService.createUser(user)
        return true
    }

}