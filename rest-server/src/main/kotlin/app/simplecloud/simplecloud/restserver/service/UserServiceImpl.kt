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

package app.simplecloud.simplecloud.restserver.service

import app.simplecloud.simplecloud.restserver.exception.ElementAlreadyExistException
import app.simplecloud.simplecloud.restserver.repository.UserRepository
import app.simplecloud.simplecloud.restserver.user.User
import com.google.inject.Inject
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 27.06.2021
 * Time: 12:44
 * @author Frederick Baier
 */
class UserServiceImpl @Inject constructor(
    private val repository: UserRepository
) : UserService {

    override fun getUserByName(name: String): CompletableFuture<User> {
        return this.repository.find(name)
    }

    override fun getAllUsers(): CompletableFuture<List<User>> {
        return this.repository.findAll()
    }

    override fun createUser(user: User) {
        if (doesUserExist(user.username)) {
            throw ElementAlreadyExistException("User does already exist")
        }
        this.repository.save(user.getIdentifier(), user)
    }

    override fun doesUserExist(username: String): Boolean {
        return runCatching { getUserByName(username) }.getOrNull() != null
    }


}