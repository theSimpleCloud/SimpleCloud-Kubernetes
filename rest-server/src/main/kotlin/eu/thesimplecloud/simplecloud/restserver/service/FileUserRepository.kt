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

import com.fasterxml.jackson.databind.ObjectMapper
import eu.thesimplecloud.simplecloud.restserver.user.User
import java.io.File
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 23.06.2021
 * Time: 23:22
 * @author Frederick Baier
 */
class FileUserRepository : IUserRepository {

    private val directory = File("test/users/")

    private val objectMapper = ObjectMapper()

    override fun findAll(): CompletableFuture<List<User>> {
        return CompletableFuture.supplyAsync {
            return@supplyAsync this.directory.listFiles().map { objectMapper.readValue(it, User::class.java) }
        }
    }

    override fun find(identifier: String): CompletableFuture<User> {
        return CompletableFuture.supplyAsync {
            val file = File(this.directory, "$identifier.json")
            return@supplyAsync this.objectMapper.readValue(file, User::class.java)
        }
    }

    override fun put(value: User) {
        val file = File(this.directory, "${value.getIdentifier()}.json")
        this.objectMapper.writeValue(file, value)
    }

    override fun remove(identifier: String) {
        val file = File(this.directory, "${identifier}.json")
        file.delete()
    }
}