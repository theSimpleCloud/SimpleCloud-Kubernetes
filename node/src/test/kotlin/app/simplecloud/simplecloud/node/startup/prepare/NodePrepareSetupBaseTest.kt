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

package app.simplecloud.simplecloud.node.startup.prepare

import app.simplecloud.simplecloud.database.api.factory.DatabaseFactory
import app.simplecloud.simplecloud.kubernetes.api.KubeAPI
import app.simplecloud.simplecloud.restserver.api.setup.RestSetupManager
import app.simplecloud.simplecloud.restserver.api.setup.Setup
import org.junit.jupiter.api.Assertions
import java.util.concurrent.CompletableFuture

/**
 * Date: 01.05.22
 * Time: 11:56
 * @author Frederick Baier
 *
 */
open class NodePrepareSetupBaseTest : AbstractNodePrepareTest() {


    private var restSetupManager = TestRestSetupManager()

    override fun setUp() {
        super.setUp()
        this.restSetupManager = TestRestSetupManager()
    }

    protected fun given(databaseFactory: DatabaseFactory, kubeAPI: KubeAPI) {
        super.given(databaseFactory, kubeAPI, restSetupManager)
    }

    protected fun assertNoSetupExecuted() {
        Assertions.assertEquals("", restSetupManager.calledWithSetupName)
    }

    protected fun assertSetupExecuted(name: String) {
        Assertions.assertEquals(name, restSetupManager.calledWithSetupName)
    }


    class TestRestSetupManager : RestSetupManager {

        var calledWithSetupName: String = ""

        override fun <T : Any> setNextSetup(setup: Setup<T>): CompletableFuture<T> {
            this.calledWithSetupName = setup.setupName
            throw RuntimeException("We are only testing if it reaches this method")
        }

        override fun setEndToken(token: String) {

        }

        override fun onEndOfAllSetups() {

        }

    }

}