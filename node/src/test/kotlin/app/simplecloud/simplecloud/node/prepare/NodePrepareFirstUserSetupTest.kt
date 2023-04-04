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

package app.simplecloud.simplecloud.node.prepare

import app.simplecloud.simplecloud.api.impl.env.VirtualEnvironmentVariables
import app.simplecloud.simplecloud.database.memory.factory.InMemoryRepositorySafeDatabaseFactory
import app.simplecloud.simplecloud.distribution.test.VirtualNetwork
import app.simplecloud.simplecloud.kubernetes.api.secret.SecretSpec
import app.simplecloud.simplecloud.kubernetes.test.KubeTestAPI
import app.simplecloud.simplecloud.node.NodeStartTestTemplate
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Date: 01.05.22
 * Time: 11:26
 * @author Frederick Baier
 *
 */
class NodePrepareFirstUserSetupTest {


    private val kubeAPI = KubeTestAPI()
    private val databaseFactory = InMemoryRepositorySafeDatabaseFactory()

    private val nodeStartTestTemplate = NodeStartTestTemplate()

    @BeforeEach
    fun setUp() {
        this.nodeStartTestTemplate.setUp()
    }

    @Test
    fun givenNormalNodeWithDatabaseConnection_willNotFail() {
        giveNodeWithDatabaseConnection()
        startNode()
    }

    @Test
    fun givenNodeWithFirstUserEnvSet_FirstUserWillBeCrated() {
        givenNodeWithEnvironmentVariable(
            mapOf(
                "INIT_USER_NAME" to "Wetterbericht",
                "INIT_USER_UUID" to "e32fda36-87d7-4520-8efb-71f11a221439",
                "INIT_USER_PASSWORD" to "MyPassword"
            )
        )
        startNode()
        assertFirstUserCreated()
    }

    private fun startNode() {
        this.nodeStartTestTemplate.startNode()
    }

    private fun assertFirstUserCreated() {
        val resourceRepository = this.databaseFactory.resourceRepository
        val resource =
            resourceRepository.load("core/v1beta1", "CloudPlayer", "name", "e32fda36-87d7-4520-8efb-71f11a221439")
        Assertions.assertNotNull(resource)
    }

    private fun giveNodeWithDatabaseConnection() {
        givenNodeWithEnvironmentVariable(emptyMap())
    }

    private fun givenNodeWithEnvironmentVariable(env: Map<String, String>) {
        this.kubeAPI.getSecretService().createSecret("database", SecretSpec().withData("database", "localhost"))
        this.nodeStartTestTemplate.given(this.kubeAPI, this.databaseFactory, VirtualEnvironmentVariables(env))
    }

    @AfterEach
    fun tearDown() {
        VirtualNetwork.reset()
    }

}