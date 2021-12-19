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

package eu.thesimplecloud.simplecloud.node.startup.setup.task

import com.ea.async.Async.await
import eu.thesimplecloud.simplecloud.api.future.completedFuture
import eu.thesimplecloud.simplecloud.restserver.setup.RestSetupManager
import eu.thesimplecloud.simplecloud.restserver.setup.body.MongoSetupResponseBody
import eu.thesimplecloud.simplecloud.restserver.setup.type.Setup
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 07/08/2021
 * Time: 00:06
 * @author Frederick Baier
 */
class MongoDbSetupTask(
    private val restSetupManager: RestSetupManager
) {

    fun run(): CompletableFuture<String> {
        val setupFuture = this.restSetupManager.setNextSetup(createSetup())
        val mongoSetupResponseBody = await(setupFuture)
        if (mongoSetupResponseBody.mongoMode == MongoSetupResponseBody.MongoMode.CREATE) {
            val newConnectionString = await(createMongoDockerContainer(mongoSetupResponseBody))
            return completedFuture(newConnectionString)
        }
        return completedFuture(mongoSetupResponseBody.connectionString)
    }

    private fun createSetup(): Setup<MongoSetupResponseBody> {
        return Setup("mongo", getMongoModePossibilities(), MongoSetupResponseBody::class)
    }

    private fun getMongoModePossibilities(): Array<MongoSetupResponseBody.MongoMode> {
        if (false) {
            return MongoSetupResponseBody.MongoMode.values()
        }
        return arrayOf(MongoSetupResponseBody.MongoMode.EXTERNAL)
    }

    private fun createMongoDockerContainer(mongoSetupResponseBody: MongoSetupResponseBody): CompletableFuture<String> {
        throw IllegalStateException("Cannot create mongodb container because docker is not available")
    }


}