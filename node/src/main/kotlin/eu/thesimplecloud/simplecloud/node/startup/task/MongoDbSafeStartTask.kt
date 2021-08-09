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

package eu.thesimplecloud.simplecloud.node.startup.task

import com.ea.async.Async
import com.ea.async.Async.await
import dev.morphia.Datastore
import eu.thesimplecloud.simplecloud.api.future.completedFuture
import eu.thesimplecloud.simplecloud.node.mongo.MongoConfigurationFileHandler
import eu.thesimplecloud.simplecloud.node.startup.NodeStartupSetupHandler
import eu.thesimplecloud.simplecloud.node.startup.setup.task.MongoDbSetupTask
import eu.thesimplecloud.simplecloud.restserver.setup.body.MongoSetupResponseBody
import eu.thesimplecloud.simplecloud.task.Task
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 09/08/2021
 * Time: 09:59
 * @author Frederick Baier
 */
class MongoDbSafeStartTask(
    private val connectionStringArgument: String?,
    private val nodeSetupHandler: NodeStartupSetupHandler
) : Task<Datastore>() {

    override fun getName(): String {
        return "mongo_safe_start"
    }

    private val mongoFileHandler = MongoConfigurationFileHandler(this.connectionStringArgument)

    override fun run(): CompletableFuture<Datastore> {
        if (!this.mongoFileHandler.isConnectionStringAvailable()) {
            await(executeMongoSetup())
        }
        return startClientAndTestConnection()
    }

    private fun startClientAndTestConnection(): CompletableFuture<Datastore> {
        val datastore = await(startMongoDbClient(mongoFileHandler.loadConnectionString()!!))
        if (isConnectedToDatabase(datastore)) {
            return completedFuture(datastore)
        }
        tryConnectionStringReset()
        return run()
    }

    private fun tryConnectionStringReset() {
        if (this.connectionStringArgument != null)
            throw IllegalArgumentException("Connection String parsed as start argument is invalid: '${connectionStringArgument}'")
        this.mongoFileHandler.deleteFile()
    }

    private fun executeMongoSetup(): CompletableFuture<String> {
        val connectionString =
            await(this.nodeSetupHandler.executeSetupTask(this.taskSubmitter) { MongoDbSetupTask(it) })
        saveResponseToFile(connectionString)
        return completedFuture(connectionString)
    }

    private fun isConnectedToDatabase(datastore: Datastore): Boolean {
        return runCatching { datastore.startSession() }.isSuccess
    }

    private fun startMongoDbClient(connectionString: String): CompletableFuture<Datastore> {
        val future = this.taskSubmitter.submit(MongoDbStartTask(connectionString))
        val mongoDatastore = await(future)
        return CompletableFuture.completedFuture(mongoDatastore)
    }

    private fun saveResponseToFile(connectionString: String) {
        this.mongoFileHandler.saveConnectionString(connectionString)
    }
}