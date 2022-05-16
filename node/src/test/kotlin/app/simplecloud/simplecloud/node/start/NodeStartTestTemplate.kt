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

package app.simplecloud.simplecloud.node.start

import app.simplecloud.simplecloud.database.api.factory.DatabaseFactory
import app.simplecloud.simplecloud.database.memory.factory.InMemoryRepositorySafeDatabaseFactory
import app.simplecloud.simplecloud.distrubtion.test.TestDistributionFactoryImpl
import app.simplecloud.simplecloud.kubernetes.api.secret.SecretSpec
import app.simplecloud.simplecloud.kubernetest.test.KubeTestAPI
import app.simplecloud.simplecloud.node.api.NodeCloudAPI
import app.simplecloud.simplecloud.node.start.restserver.FailingRestSetupManager
import app.simplecloud.simplecloud.node.start.restserver.TestRestServer
import app.simplecloud.simplecloud.node.start.restserver.TestTokenHandlerFactory
import app.simplecloud.simplecloud.node.startup.NodeStartup
import app.simplecloud.simplecloud.restserver.api.RestServerConfig
import app.simplecloud.simplecloud.restserver.api.auth.NoAuthService
import app.simplecloud.simplecloud.restserver.impl.controller.ControllerHandlerFactoryImpl

/**
 * Date: 11.05.22
 * Time: 10:11
 * @author Frederick Baier
 *
 */
class NodeStartTestTemplate {


    private lateinit var restServerConfig: RestServerConfig

    var kubeAPI = KubeTestAPI()
        private set
    private var databaseFactory: DatabaseFactory = InMemoryRepositorySafeDatabaseFactory()


    fun setUp() {
        setUpRestServer()
        this.kubeAPI = KubeTestAPI()
        this.databaseFactory = InMemoryRepositorySafeDatabaseFactory()
    }

    fun givenKubeAPIWithDatabaseConnection() {
        val secretService = this.kubeAPI.getSecretService()
        secretService.createSecret("database", SecretSpec().withData("database", "localhost"))
    }

    fun given(databaseFactory: DatabaseFactory) {
        this.databaseFactory = databaseFactory
    }

    fun given(kubeAPI: KubeTestAPI, databaseFactory: DatabaseFactory) {
        this.kubeAPI = kubeAPI
        this.databaseFactory = databaseFactory
    }

    private fun setUpRestServer() {
        val restServer = TestRestServer(NoAuthService())
        this.restServerConfig = RestServerConfig(
            restServer,
            TestTokenHandlerFactory(),
            ControllerHandlerFactoryImpl(),
            FailingRestSetupManager()
        )
    }

    fun startNode(): NodeCloudAPI {
        return NodeStartup(
            this.databaseFactory,
            TestDistributionFactoryImpl(),
            this.kubeAPI,
            restServerConfig
        ).start()
    }

}