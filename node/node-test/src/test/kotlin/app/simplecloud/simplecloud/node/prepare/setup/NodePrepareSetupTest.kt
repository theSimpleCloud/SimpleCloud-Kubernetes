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

package app.simplecloud.simplecloud.node.prepare.setup

import app.simplecloud.simplecloud.database.memory.factory.InMemoryDatabaseFactory
import app.simplecloud.simplecloud.kubernetes.api.secret.SecretSpec
import app.simplecloud.simplecloud.kubernetes.test.KubeTestAPI
import app.simplecloud.simplecloud.node.DatabaseFactoryProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Date: 01.05.22
 * Time: 11:26
 * @author Frederick Baier
 *
 */
class NodePrepareSetupTest : NodePrepareSetupBaseTest() {

    @BeforeEach
    override fun setUp() {
        super.setUp()
    }

    @Test
    internal fun emptyNode_willExecuteDatabaseSetup() {
        givenEmptyNode()
        prepareNode()
        assertSetupExecuted("database")
    }

    @Test
    internal fun databaseConfigGiven_WillExecuteFirstUserSetup() {
        givenNodeWithDatabaseConnection()
        prepareNode()
        assertSetupExecuted("firstuser")
    }

    @Test
    internal fun givenDatabaseAndFirstUser_WillNotExecuteSetup() {
        givenDatabaseAndFirstUser()
        prepareNode()
        assertNoSetupExecuted()
    }

    private fun givenDatabaseAndFirstUser() {
        val kubeApi = KubeTestAPI()
        kubeApi.getSecretService().createSecret("database", SecretSpec().withData("database", "localhost"))
        given(DatabaseFactoryProvider().withFirstUser().get(), kubeApi)
    }


    private fun givenNodeWithDatabaseConnection() {
        val kubeApi = KubeTestAPI()
        kubeApi.getSecretService().createSecret("database", SecretSpec().withData("database", "localhost"))
        given(InMemoryDatabaseFactory(), kubeApi)
    }

    private fun givenEmptyNode() {
        given(InMemoryDatabaseFactory(), KubeTestAPI())
    }


}