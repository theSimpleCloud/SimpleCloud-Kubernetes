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
import org.junit.jupiter.api.*

/**
 * Date: 09.04.22
 * Time: 11:04
 * @author Frederick Baier
 *
 */
@Disabled
class DistributionCacheTest {

    private lateinit var factory: DistributionFactory

    private var server: Distribution? = null
    private var client: Distribution? = null

    @BeforeEach
    fun setUp() {
        this.factory = HazelcastDistributionFactory()
    }

    @AfterEach
    fun tearDown() {
        server?.shutdown()
        client?.shutdown()
    }

    @Test
    fun onlyOneServer() {
        this.server = this.factory.createServer(1630, emptyList())
        val cache = server!!.getOrCreateCache<String, String>("test")
        cache["test1"] = "test2"
        Assertions.assertEquals("test2", cache["test1"])
    }

    @Test
    fun serverAndClient_cacheIsSynchronized() {
        this.server = this.factory.createServer(1630, emptyList())
        this.client = this.factory.createClient(Address("127.0.0.1", 1630))
        val serverCache = server!!.getOrCreateCache<String, String>("test")
        val clientCache = client!!.getOrCreateCache<String, String>("test")
        serverCache["test1"] = "test2"
        Assertions.assertEquals("test2", clientCache["test1"])
        clientCache.remove("test1")
        Assertions.assertNull(serverCache["test1"])
    }

    @Test
    fun separateCache_areIndependent() {
        this.server = this.factory.createServer(1630, emptyList())
        this.client = this.factory.createClient(Address("127.0.0.1", 1630))
        val serverCache = server!!.getOrCreateCache<String, String>("one")
        val clientCache = client!!.getOrCreateCache<String, String>("two")
        serverCache["test1"] = "test2"
        Assertions.assertNull(clientCache["test1"])
        clientCache.remove("test1")
        Assertions.assertNotNull(serverCache["test1"])
    }

}