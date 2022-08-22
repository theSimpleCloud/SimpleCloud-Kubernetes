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

package app.simplecloud.simplecloud.node.database

import app.simplecloud.simplecloud.api.future.CloudCompletableFuture
import app.simplecloud.simplecloud.database.memory.factory.InMemoryDatabaseFactory
import app.simplecloud.simplecloud.kubernetes.test.KubeTestAPI
import app.simplecloud.simplecloud.node.prepare.setup.AbstractNodePrepareTest
import app.simplecloud.simplecloud.node.startup.setup.body.DatabaseSetupResponseBody
import app.simplecloud.simplecloud.restserver.api.setup.RestSetupManager
import app.simplecloud.simplecloud.restserver.api.setup.Setup
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture

/**
 * Date: 01.05.22
 * Time: 11:26
 * @author Frederick Baier
 *
 */
class NodePrepareDatabaseSetupTest : AbstractNodePrepareTest() {


    private val kubeAPI = KubeTestAPI()

    @BeforeEach
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun databaseSetupTest() {
        givenNodeWithDatabaseSetupAutoComplete()
        prepareNode()
        assertDatabaseSecretWasCreated()
    }

    private fun assertDatabaseSecretWasCreated() {
        val secretService = this.kubeAPI.getSecretService()
        secretService.getSecret("database")
    }

    private fun givenNodeWithDatabaseSetupAutoComplete() {
        given(InMemoryDatabaseFactory(), this.kubeAPI, DatabaseRestSetupManager())
    }


    class DatabaseRestSetupManager : RestSetupManager {

        override fun <T : Any> setNextSetup(setup: Setup<T>): CompletableFuture<T> {
            return CloudCompletableFuture.completedFuture(
                DatabaseSetupResponseBody("localhost", DatabaseSetupResponseBody.DatabaseMode.EXTERNAL)
            ) as CompletableFuture<T>
        }

        override fun setEndToken(token: String) {

        }

        override fun onEndOfAllSetups() {

        }

    }

}