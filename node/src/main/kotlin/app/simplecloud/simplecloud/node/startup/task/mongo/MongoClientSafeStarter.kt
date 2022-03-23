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

package app.simplecloud.simplecloud.node.startup.task.mongo

import app.simplecloud.simplecloud.kubernetes.api.secret.KubeSecret
import app.simplecloud.simplecloud.kubernetes.api.secret.KubeSecretService
import app.simplecloud.simplecloud.kubernetes.api.secret.SecretSpec
import app.simplecloud.simplecloud.node.startup.setup.task.MongoDbSetup
import app.simplecloud.simplecloud.restserver.setup.RestSetupManager
import com.google.inject.Inject
import dev.morphia.Datastore
import org.apache.logging.log4j.LogManager

/**
 * Created by IntelliJ IDEA.
 * Date: 09/08/2021
 * Time: 09:59
 * @author Frederick Baier
 */
class MongoClientSafeStarter @Inject constructor(
    private val restSetupManager: RestSetupManager,
    private val kubeSecretService: KubeSecretService
) {

    fun startMongoClient(): Datastore {
        logger.info("Starting MongoDB")
        if (!isSecretAvailable()) {
            executeMongoSetup()
        }
        return startClientAndTestConnection()
    }

    private fun isSecretAvailable(): Boolean {
        return runCatching { this.kubeSecretService.getSecret(MONGO_SECRET_NAME) }.isSuccess
    }

    private fun startClientAndTestConnection(): Datastore {
        val secret = loadSecret()
        val connectionString = secret.getStringValueOf("mongo")
        val datastore = startMongoDbClient(connectionString)
        if (isConnectedToDatabase(datastore)) {
            logger.info("Connected to database")
            return datastore
        }
        throw IllegalArgumentException("Connection String is invalid ${connectionString}")
    }

    private fun loadSecret(): KubeSecret {
        return this.kubeSecretService.getSecret(MONGO_SECRET_NAME)
    }

    private fun executeMongoSetup(): String {
        val connectionString = MongoDbSetup(this.restSetupManager).executeSetup()
        saveResponseToSecret(connectionString)
        return connectionString
    }

    private fun isConnectedToDatabase(datastore: Datastore): Boolean {
        return runCatching { datastore.startSession() }.isSuccess
    }

    private fun startMongoDbClient(connectionString: String): Datastore {
        return MongoDBClientStarter(connectionString).startClient()
    }

    private fun saveResponseToSecret(connectionString: String) {
        this.kubeSecretService.createSecret(MONGO_SECRET_NAME, SecretSpec().withData("mongo", connectionString))
    }

    companion object {
        const val MONGO_SECRET_NAME = "mongo"
        private val logger = LogManager.getLogger(MongoClientSafeStarter::class.java)
    }

}