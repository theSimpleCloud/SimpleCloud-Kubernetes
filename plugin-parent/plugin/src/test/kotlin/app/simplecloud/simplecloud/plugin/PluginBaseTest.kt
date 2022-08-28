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

package app.simplecloud.simplecloud.plugin

import app.simplecloud.simplecloud.database.memory.factory.InMemoryRepositorySafeDatabaseFactory
import app.simplecloud.simplecloud.kubernetes.api.KubeAPI
import app.simplecloud.simplecloud.kubernetes.api.Label
import app.simplecloud.simplecloud.kubernetes.api.service.ServiceSpec
import app.simplecloud.simplecloud.node.api.NodeAPIBaseTest
import app.simplecloud.simplecloud.node.api.NodeCloudAPI
import app.simplecloud.simplecloud.node.api.NodeCloudAPIImpl
import app.simplecloud.simplecloud.node.util.TestProcessProvider
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

/**
 * Date: 24.05.22
 * Time: 22:15
 * @author Frederick Baier
 *
 */
open class PluginBaseTest : TestProcessProvider {

    private lateinit var nodeAPIBaseTest: NodeAPIBaseTest
    protected lateinit var nodeCloudAPI: NodeCloudAPIImpl
    protected lateinit var kubeAPI: KubeAPI
    protected lateinit var databaseFactory: InMemoryRepositorySafeDatabaseFactory

    @BeforeEach
    internal open fun setUp() {
        this.nodeAPIBaseTest = NodeAPIBaseTest()
        this.nodeAPIBaseTest.setUp()
        this.nodeCloudAPI = this.nodeAPIBaseTest.cloudAPI
        this.kubeAPI = this.nodeAPIBaseTest.kubeAPI
        this.databaseFactory = this.nodeAPIBaseTest.databaseFactory
        createDistributionService()
    }

    @AfterEach
    fun tearDown() {
        this.nodeAPIBaseTest.tearDown()
    }

    private fun createDistributionService() {
        this.kubeAPI.getNetworkService().createService(
            "distribution",
            ServiceSpec()
                .withClusterPort(1670)
                .withContainerPort(1670)
                .withLabels(Label("app", "simplecloud"))
        )
    }

    override fun getCloudAPI(): NodeCloudAPI {
        return this.nodeCloudAPI
    }
}