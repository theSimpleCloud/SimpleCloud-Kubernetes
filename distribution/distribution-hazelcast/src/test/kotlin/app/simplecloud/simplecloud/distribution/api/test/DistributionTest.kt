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
import app.simplecloud.simplecloud.distribution.api.Distribution
import app.simplecloud.simplecloud.distribution.api.DistributionFactory
import app.simplecloud.simplecloud.distribution.hazelcast.HazelcastDistributionFactory
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import java.net.ConnectException

/**
 * Date: 07.04.22
 * Time: 13:02
 * @author Frederick Baier
 *
 */
@Disabled
class DistributionTest {

    private lateinit var factory: DistributionFactory

    private var server: Distribution? = null
    private var server2: Distribution? = null
    private var server3: Distribution? = null
    private var client: Distribution? = null

    @BeforeEach
    fun setUp() {
        this.factory = HazelcastDistributionFactory()
    }

    @AfterEach
    fun tearDown() {
        this.server?.shutdown()
        this.server2?.shutdown()
        this.server3?.shutdown()
        this.client?.shutdown()
    }

    @Test
    fun newDistributionServer_hasOneServerAndZeroClients() {
        this.server = factory.createServer(1330, emptyList())
        assertEquals(1, this.server!!.getServers().size)
        assertEquals(0, this.server!!.getConnectedClients().size)
    }

    @Test
    fun newClientWithoutServer_WillThrowError() {
        assertThrows<ConnectException> {
            factory.createClient(Address("127.0.0.1", 1630))
        }
    }

    @Test
    fun newClientWithWrongAddressAndServer_wilThrowError() {
        this.server = factory.createServer(1630, emptyList())
        assertThrows<ConnectException> {
            factory.createClient(Address("127.0.0.1", 1631))
        }
    }

    @Test
    fun newClientWithServer_wilNotThrowError() {
        this.server = factory.createServer(1630, emptyList())
        this.client = factory.createClient(Address("127.0.0.1", 1630))
    }

    @Test
    fun newClientWithServer_HasOneClientAndOneServer() {
        this.server = factory.createServer(1630, emptyList())
        this.client = factory.createClient(Address("127.0.0.1", 1630))
        assertEquals(1, client!!.getServers().size)
        assertEquals(1, server!!.getServers().size)
        assertEquals(1, server!!.getConnectedClients().size)
    }

    @Test
    fun newServerWithNotExistingServerToConnectTo_willNotThrow() {
        this.server = factory.createServer(1630, listOf(Address("127.0.0.1", 1631)))
        assertEquals(1, server!!.getServers().size)
        assertEquals(0, server!!.getConnectedClients().size)
    }

    @Test
    fun serverConnectToServer() {
        this.server = factory.createServer(1630, emptyList())
        this.server2 = factory.createServer(1631, listOf(Address("127.0.0.1", 1630)))
        assertEquals(2, server!!.getServers().size)
        assertEquals(2, server2!!.getServers().size)
    }

    @Test
    fun twoServerAndClient() {
        this.server = factory.createServer(1630, emptyList())
        this.server2 = factory.createServer(1631, listOf(Address("127.0.0.1", 1630)))
        this.client = factory.createClient(Address("127.0.0.1", 1630))
        assertEquals(2, server!!.getServers().size)
        assertEquals(2, server2!!.getServers().size)
        assertEquals(2, client!!.getServers().size)
    }

    @Test
    fun serverAndClientCluster_ServerJoin_HaveNewServer() {
        this.server = factory.createServer(1630, emptyList())
        this.client = factory.createClient(Address("127.0.0.1", 1630))
        this.server2 = factory.createServer(1631, listOf(Address("127.0.0.1", 1630)))
        assertEquals(2, client!!.getServers().size)
        assertEquals(2, client!!.getServers().size)
        assertEquals(2, server2!!.getServers().size)
    }

}