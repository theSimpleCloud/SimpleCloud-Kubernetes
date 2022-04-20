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

package app.simplecloud.simplecloud.distribution.api.test

import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.distribution.api.DistributionFactory
import app.simplecloud.simplecloud.distribution.api.TestDistributionFactoryImpl
import app.simplecloud.simplecloud.distribution.api.VirtualNetwork
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.net.ConnectException

/**
 * Date: 07.04.22
 * Time: 13:02
 * @author Frederick Baier
 *
 */
class DistributionTest {

    private lateinit var factory: DistributionFactory

    @BeforeEach
    fun setUp() {
        this.factory = TestDistributionFactoryImpl()
    }

    @AfterEach
    fun tearDown() {
        VirtualNetwork.reset()
    }

    @Test
    fun newDistributionServer_hasOneServerAndZeroClients() {
        val distribution = factory.createServer(1330, emptyList())
        assertEquals(1, distribution.getServers().size)
        assertEquals(0, distribution.getConnectedClients().size)
    }

    @Test
    fun newClientWithoutServer_WillThrowError() {
        assertThrows<ConnectException> {
            factory.createClient(Address("127.0.0.1", 1630))
        }
    }

    @Test
    fun newClientWithWrongAddressAndServer_wilThrowError() {
        factory.createServer(1630, emptyList())
        assertThrows<ConnectException> {
            factory.createClient(Address("127.0.0.1", 1631))
        }
    }

    @Test
    fun newClientWithServer_wilNotThrowError() {
        factory.createServer(1630, emptyList())
        factory.createClient(Address("127.0.0.1", 1630))
    }

    @Test
    fun newClientWithServer_HasOneClientAndOneServer() {
        val factory = TestDistributionFactoryImpl()
        val server = factory.createServer(1630, emptyList())
        val clientDistribution = factory.createClient(Address("127.0.0.1", 1630))
        assertEquals(1, clientDistribution.getServers().size)
        assertEquals(1, server.getServers().size)
        assertEquals(1, server.getConnectedClients().size)
    }

    @Test
    fun newServerWithNotExistingServerToConnectTo_willNotThrow() {
        val factory = TestDistributionFactoryImpl()
        val server = factory.createServer(1630, listOf(Address("127.0.0.1", 1631)))
        assertEquals(1, server.getServers().size)
        assertEquals(0, server.getConnectedClients().size)
    }

    @Test
    fun serverConnectToServer() {
        val factory = TestDistributionFactoryImpl()
        val server1 = factory.createServer(1630, emptyList())
        val server2 = factory.createServer(1631, listOf(Address("127.0.0.1", 1630)))
        assertEquals(2, server1.getServers().size)
        assertEquals(2, server2.getServers().size)
    }

    @Test
    fun twoServerAndClient() {
        val factory = TestDistributionFactoryImpl()
        val server1 = factory.createServer(1630, emptyList())
        val server2 = factory.createServer(1631, listOf(Address("127.0.0.1", 1630)))
        val clientDistribution = factory.createClient(Address("127.0.0.1", 1630))
        assertEquals(2, server1.getServers().size)
        assertEquals(2, server2.getServers().size)
        assertEquals(2, clientDistribution.getServers().size)
    }

    @Test
    fun serverAndClientCluster_ServerJoin_HaveNewServer() {
        val factory = TestDistributionFactoryImpl()
        val server1 = factory.createServer(1630, emptyList())
        val clientDistribution = factory.createClient(Address("127.0.0.1", 1630))
        val server2 = factory.createServer(1631, listOf(Address("127.0.0.1", 1630)))
        assertEquals(2, server1.getServers().size)
        assertEquals(2, clientDistribution.getServers().size)
        assertEquals(2, server2.getServers().size)
    }

}