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