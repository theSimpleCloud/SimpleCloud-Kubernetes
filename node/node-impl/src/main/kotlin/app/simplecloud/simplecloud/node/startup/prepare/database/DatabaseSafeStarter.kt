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

package app.simplecloud.simplecloud.node.startup.prepare.database

import app.simplecloud.simplecloud.database.api.factory.DatabaseFactory
import app.simplecloud.simplecloud.database.api.factory.DatabaseRepositories
import app.simplecloud.simplecloud.kubernetes.api.secret.KubeSecret
import app.simplecloud.simplecloud.kubernetes.api.secret.KubeSecretService
import app.simplecloud.simplecloud.kubernetes.api.secret.SecretSpec
import app.simplecloud.simplecloud.node.startup.setup.task.DatabaseSetup
import app.simplecloud.simplecloud.restserver.api.setup.RestSetupManager
import org.apache.logging.log4j.LogManager

/**
 * Created by IntelliJ IDEA.
 * Date: 09/08/2021
 * Time: 09:59
 * @author Frederick Baier
 */
class DatabaseSafeStarter constructor(
    private val restSetupManager: RestSetupManager,
    private val kubeSecretService: KubeSecretService,
    private val databaseFactory: DatabaseFactory
) {

    fun connectToDatabase(): DatabaseRepositories {
        logger.info("Starting Database Client")
        if (!isSecretAvailable()) {
            executeDatabaseSetup()
        }
        return startClientAndTestConnection()
    }

    private fun isSecretAvailable(): Boolean {
        return runCatching { this.kubeSecretService.getSecret(DATABASE_SECRET_NAME) }.isSuccess
    }

    private fun startClientAndTestConnection(): DatabaseRepositories {
        val secret = loadSecret()
        val connectionString = secret.getStringValueOf("database")
        return this.databaseFactory.create(connectionString)
    }

    private fun loadSecret(): KubeSecret {
        return this.kubeSecretService.getSecret(DATABASE_SECRET_NAME)
    }

    private fun executeDatabaseSetup(): String {
        val connectionString = DatabaseSetup(this.restSetupManager).executeSetup()
        saveResponseToSecret(connectionString)
        return connectionString
    }

    private fun saveResponseToSecret(connectionString: String) {
        this.kubeSecretService.createSecret(DATABASE_SECRET_NAME, SecretSpec().withData("database", connectionString))
    }

    companion object {
        const val DATABASE_SECRET_NAME = "database"
        private val logger = LogManager.getLogger(DatabaseSafeStarter::class.java)
    }

}