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

package app.simplecloud.simplecloud.node

import app.simplecloud.simplecloud.api.impl.env.EnvironmentVariables
import app.simplecloud.simplecloud.api.impl.env.VirtualEnvironmentVariables
import app.simplecloud.simplecloud.database.api.factory.DatabaseFactory
import app.simplecloud.simplecloud.database.memory.factory.InMemoryRepositorySafeDatabaseFactory
import app.simplecloud.simplecloud.distribution.test.TestDistributionFactoryImpl
import app.simplecloud.simplecloud.kubernetes.api.Label
import app.simplecloud.simplecloud.kubernetes.api.pod.PodSpec
import app.simplecloud.simplecloud.kubernetes.api.secret.SecretSpec
import app.simplecloud.simplecloud.kubernetes.test.KubeTestAPI
import app.simplecloud.simplecloud.module.api.impl.NodeCloudAPIImpl
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
    private var environmentVariables: EnvironmentVariables = VirtualEnvironmentVariables(emptyMap())


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

    fun given(kubeAPI: KubeTestAPI, databaseFactory: DatabaseFactory, environmentVariables: EnvironmentVariables) {
        this.kubeAPI = kubeAPI
        this.databaseFactory = databaseFactory
        this.environmentVariables = environmentVariables
    }

    private fun setUpRestServer() {
        val restServer = TestRestServer(NoAuthService())
        this.restServerConfig = RestServerConfig(
            restServer,
            TestTokenHandlerFactory(),
            ControllerHandlerFactoryImpl()
        )
    }

    fun startNode(): NodeCloudAPIImpl {
        val selfPod = createNodeSelfPod()
        return NodeStartup(
            this.databaseFactory,
            TestDistributionFactoryImpl(),
            this.kubeAPI,
            selfPod,
            restServerConfig,
            this.environmentVariables
        ).start()
    }

    private fun createNodeSelfPod() =
        this.kubeAPI.getPodService().createPod(
            "Node",
            PodSpec()
                .withLabels(Label("app", "simplecloud"))
                .withContainerPort(1670)
        )

}