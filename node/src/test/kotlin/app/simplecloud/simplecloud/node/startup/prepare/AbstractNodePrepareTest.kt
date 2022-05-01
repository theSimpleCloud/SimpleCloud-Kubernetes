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
import app.simplecloud.simplecloud.database.memory.factory.InMemoryDatabaseFactory
import app.simplecloud.simplecloud.kubernetes.api.KubeAPI
import app.simplecloud.simplecloud.kubernetest.test.KubeTestAPI
import app.simplecloud.simplecloud.restserver.setup.RestSetupManager

/**
 * Date: 01.05.22
 * Time: 11:56
 * @author Frederick Baier
 *
 */
abstract class AbstractNodePrepareTest {


    private var databaseFactory: DatabaseFactory = InMemoryDatabaseFactory()
    private var kubeAPI: KubeAPI = KubeTestAPI()
    private lateinit var restSetupManager: RestSetupManager

    internal open fun setUp() {
        this.databaseFactory = InMemoryDatabaseFactory()
        this.kubeAPI = KubeTestAPI()
    }

    protected fun given(databaseFactory: DatabaseFactory, kubeAPI: KubeAPI, restSetupManager: RestSetupManager) {
        this.databaseFactory = databaseFactory
        this.kubeAPI = kubeAPI
        this.restSetupManager = restSetupManager
    }

    protected fun prepareNode() {
        val nodePreparer = NodePreparer(this.databaseFactory, this.kubeAPI, this.restSetupManager)
        try {
            nodePreparer.prepare()
        } catch (e: Exception) {
            //ignore because we are only testing if it reaches the setup
        }
    }

}