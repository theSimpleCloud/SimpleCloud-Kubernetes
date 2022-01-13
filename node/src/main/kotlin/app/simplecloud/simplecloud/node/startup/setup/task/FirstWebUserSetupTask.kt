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

package app.simplecloud.simplecloud.node.startup.setup.task

import app.simplecloud.simplecloud.api.future.unitFuture
import app.simplecloud.simplecloud.restserver.repository.UserRepository
import app.simplecloud.simplecloud.restserver.setup.RestSetupManager
import app.simplecloud.simplecloud.restserver.setup.body.FirstUserSetupResponseBody
import app.simplecloud.simplecloud.restserver.setup.type.Setup
import app.simplecloud.simplecloud.restserver.user.User
import com.ea.async.Async.await
import org.apache.logging.log4j.LogManager
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 07/08/2021
 * Time: 00:06
 * @author Frederick Baier
 */
class FirstWebUserSetupTask(
    private val restSetupManager: RestSetupManager,
    private val userRepository: UserRepository
) {

    fun run(): CompletableFuture<Unit> {
        logger.info("Executing First User Setup")
        val setupFuture = this.restSetupManager.setNextSetup(Setup.FIRST_USER)
        val responseBody = await(setupFuture)
        saveResponseToMongoDatabase(responseBody)
        return unitFuture()
    }

    private fun saveResponseToMongoDatabase(response: FirstUserSetupResponseBody) {
        val user = User(response.username, response.password)
        this.userRepository.save(user.getIdentifier(), user)
    }

    companion object {
        private val logger = LogManager.getLogger(FirstWebUserSetupTask::class.java)
    }

}