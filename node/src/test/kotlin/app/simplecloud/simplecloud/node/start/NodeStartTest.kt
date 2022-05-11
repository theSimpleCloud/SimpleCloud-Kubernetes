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

import app.simplecloud.simplecloud.distrubtion.test.VirtualNetwork
import app.simplecloud.simplecloud.node.DatabaseFactoryProvider
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Date: 09.05.22
 * Time: 23:35
 * @author Frederick Baier
 *
 */
class NodeStartTest {

    private val nodeStartTestTemplate = NodeStartTestTemplate()
    private var databaseFactoryProvider = DatabaseFactoryProvider()


    @BeforeEach
    internal fun setUp() {
        nodeStartTestTemplate.setUp()
        this.databaseFactoryProvider = DatabaseFactoryProvider()
    }

    @AfterEach
    internal fun tearDown() {
        VirtualNetwork.reset()
    }

    @Test
    fun startWithNoSetupsNeeded_willNotFail() {
        val databaseFactory = this.databaseFactoryProvider.withFirstUser().get()
        nodeStartTestTemplate.given(databaseFactory)
        nodeStartTestTemplate.givenKubeAPIWithDatabaseConnection()
        nodeStartTestTemplate.startNode()
    }

    @Test
    fun test() {
        val databaseFactory = this.databaseFactoryProvider
            .withFirstUser()
            .withProxyGroup("Proxy")
            .withMinOnlineStrategy("Min", "Proxy", 1)
            .get()
        nodeStartTestTemplate.given(databaseFactory)
        nodeStartTestTemplate.givenKubeAPIWithDatabaseConnection()
        nodeStartTestTemplate.startNode()
    }

}