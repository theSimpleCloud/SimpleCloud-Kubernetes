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

package app.simplecloud.simplecloud.node.api

import app.simplecloud.simplecloud.database.memory.factory.InMemoryRepositorySafeDatabaseFactory
import app.simplecloud.simplecloud.distribution.test.VirtualNetwork
import app.simplecloud.simplecloud.kubernetes.test.KubeTestAPI
import app.simplecloud.simplecloud.module.api.impl.NodeCloudAPIImpl
import app.simplecloud.simplecloud.node.DatabaseFactoryProvider
import app.simplecloud.simplecloud.node.NodeStartTestTemplate

/**
 * Date: 11.05.22
 * Time: 17:33
 * @author Frederick Baier
 *
 */
open class NodeAPIBaseTest {

    private val nodeStartTestTemplate = NodeStartTestTemplate()

    lateinit var kubeAPI: KubeTestAPI
        private set

    lateinit var cloudAPI: NodeCloudAPIImpl
        private set

    var databaseFactory = InMemoryRepositorySafeDatabaseFactory()
        private set

    open fun setUp() {
        this.nodeStartTestTemplate.setUp()
        this.nodeStartTestTemplate.givenKubeAPIWithDatabaseConnection()
        this.databaseFactory = DatabaseFactoryProvider().withFirstUser().get()
        this.nodeStartTestTemplate.given(this.databaseFactory)
        this.cloudAPI = this.nodeStartTestTemplate.startNode()
        this.kubeAPI = nodeStartTestTemplate.kubeAPI
    }

    open fun tearDown() {
        VirtualNetwork.reset()
    }

}