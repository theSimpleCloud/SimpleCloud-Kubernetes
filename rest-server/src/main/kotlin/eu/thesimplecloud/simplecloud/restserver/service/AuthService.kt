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

package eu.thesimplecloud.simplecloud.restserver.service

import com.google.inject.Inject
import com.google.inject.Singleton
import eu.thesimplecloud.simplecloud.api.impl.future.getOrThrowRealExceptionOnFailure
import eu.thesimplecloud.simplecloud.restserver.exception.NotAuthenticatedException
import eu.thesimplecloud.simplecloud.restserver.jwt.JwtConfig
import eu.thesimplecloud.simplecloud.restserver.repository.IUserRepository
import eu.thesimplecloud.simplecloud.restserver.user.User
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*

/**
 * Created by IntelliJ IDEA.
 * Date: 23.06.2021
 * Time: 14:52
 * @author Frederick Baier
 */
@Singleton
class AuthService @Inject constructor(
    private val userRepository: IUserRepository
) : IAuthService {

    override fun authenticate(usernameAndPasswordCredentials: UsernameAndPasswordCredentials): String {
        val user = findUserByUserName(usernameAndPasswordCredentials.username)
        if (user.password != usernameAndPasswordCredentials.password) {
            throwUserNotFound()
        }

        return JwtConfig.makeToken(user.username)
    }

    override fun getUserFromCall(call: ApplicationCall): User {
        val principal = call.principal<JWTPrincipal>()
            ?: throw NotAuthenticatedException()
        val username = principal.getClaim("username", String::class)!!
        return findUserByUserName(username)
    }

    private fun findUserByUserName(username: String): User {
        return this.userRepository.find(username).getOrThrowRealExceptionOnFailure()
    }

    private fun throwUserNotFound(): Nothing {
        throw NoSuchElementException("User not found")
    }

}

