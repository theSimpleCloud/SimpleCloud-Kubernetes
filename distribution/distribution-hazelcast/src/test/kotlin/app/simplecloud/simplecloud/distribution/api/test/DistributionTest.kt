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

import app.simplecloud.simplecloud.api.utils.Address
import app.simplecloud.simplecloud.distibution.hazelcast.HazelcastDistributionFactory
import app.simplecloud.simplecloud.distribution.api.Distribution
import app.simplecloud.simplecloud.distribution.api.DistributionFactory
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

    private var client: Distribution? = null
    private var server: Distribution? = null
    private var server2: Distribution? = null

    @BeforeEach
    fun setUp() {
        this.factory = HazelcastDistributionFactory()
    }

    @AfterEach
    fun tearDown() {
        this.client?.shutdown()
        this.server?.shutdown()
        this.server2?.shutdown()
    }

    @Test
    fun newDistributionServer_hasOneMember() {
        server = factory.createServer(1330, emptyList())
        val members = server!!.getMembers()
        assertEquals(1, members.size)
    }

    @Test
    fun newClientWithoutServer_WillThrowError() {
        assertThrows<ConnectException> {
            client = factory.createClient(Address("127.0.0.1", 1630))
        }
    }

    @Test
    fun newClientWithWrongAddressAndServer_wilThrowError() {
        server = factory.createServer(1630, emptyList())
        assertThrows<ConnectException> {
            client = factory.createClient(Address("127.0.0.1", 1631))
        }
    }

    @Test
    fun newClientWithServer_wilNotThrowError() {
        server = factory.createServer(1630, emptyList())
        client = factory.createClient(Address("127.0.0.1", 1630))
    }

    @Test
    fun newClientWithServer_membersSizeIsTwo() {
        server = factory.createServer(1630, emptyList())
        client = factory.createClient(Address("127.0.0.1", 1630))
        assertEquals(2, client!!.getMembers().size)
    }

    @Test
    fun newServerWithNotExistingServerToConnectTo_willNotThrow() {
        server = factory.createServer(1630, listOf(Address("127.0.0.1", 1631)))
        assertEquals(1, server!!.getMembers().size)
    }

    @Test
    fun serverConnectToServer() {
        server = factory.createServer(1630, emptyList())
        server2 = factory.createServer(1631, listOf(Address("127.0.0.1", 1630)))
        assertEquals(2, server!!.getMembers().size)
        assertEquals(2, server2!!.getMembers().size)
    }

    @Test
    fun twoServerAndClient() {
        server = factory.createServer(1630, emptyList())
        server2 = factory.createServer(1631, listOf(Address("127.0.0.1", 1630)))
        client = factory.createClient(Address("127.0.0.1", 1630))
        assertEquals(3, server!!.getMembers().size)
        assertEquals(3, server2!!.getMembers().size)
        assertEquals(3, client!!.getMembers().size)
    }

}