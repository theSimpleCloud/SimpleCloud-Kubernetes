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

import app.simplecloud.simplecloud.distibution.hazelcast.HazelcastDistributionFactory
import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.distribution.api.Distribution
import app.simplecloud.simplecloud.distribution.api.DistributionFactory
import app.simplecloud.simplecloud.distribution.api.EntryListener
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

    @Test
    fun listenerRegistered_NoItemGetsAdded_WillNotFire() {
        this.server = this.factory.createServer(1630, emptyList())
        val serverCache = server!!.getOrCreateCache<String, String>("one")
        var hasFired = false
        val entryListener = object : EntryListener<String, String> {
            override fun entryUpdated(entry: Pair<String, String>) {
                hasFired = true
            }

            override fun entryRemoved(entry: Pair<String, String>) {

            }
        }
        serverCache.addEntryListener(entryListener)
        Assertions.assertFalse(hasFired)
    }

    @Test
    fun listenerRegistered_ItemGetsAdded_WillFire() {
        this.server = this.factory.createServer(1630, emptyList())
        val serverCache = server!!.getOrCreateCache<String, String>("one")
        var hasFired = false
        val entryListener = object : EntryListener<String, String> {
            override fun entryUpdated(entry: Pair<String, String>) {
                hasFired = true
            }

            override fun entryRemoved(entry: Pair<String, String>) {

            }
        }
        serverCache.addEntryListener(entryListener)
        serverCache.put("test", "value")
        Thread.sleep(500)
        Assertions.assertTrue(hasFired)
    }

    @Test
    fun listenerRegistered_NotExistingItemGetsRemoved_WillNotFire() {
        this.server = this.factory.createServer(1630, emptyList())
        val serverCache = server!!.getOrCreateCache<String, String>("one")
        var hasFired = false
        val entryListener = object : EntryListener<String, String> {
            override fun entryUpdated(entry: Pair<String, String>) {
            }

            override fun entryRemoved(entry: Pair<String, String>) {
                hasFired = true
            }
        }
        serverCache.addEntryListener(entryListener)
        serverCache.remove("test", "value")
        Thread.sleep(500)
        Assertions.assertFalse(hasFired)
    }

    @Test
    fun listenerRegistered_ItemGetsRemoved_WillFire() {
        this.server = this.factory.createServer(1630, emptyList())
        val serverCache = server!!.getOrCreateCache<String, String>("one")
        var hasFired = false
        val entryListener = object : EntryListener<String, String> {
            override fun entryUpdated(entry: Pair<String, String>) {
            }

            override fun entryRemoved(entry: Pair<String, String>) {
                hasFired = true
            }
        }
        serverCache.addEntryListener(entryListener)
        serverCache.put("test", "value")
        serverCache.remove("test")
        Thread.sleep(500)
        Assertions.assertTrue(hasFired)
    }

    @Test
    fun itemOnServerGetsAdded_WillFireOnClient() {
        this.server = this.factory.createServer(1630, emptyList())
        this.client = this.factory.createClient(Address("127.0.0.1", 1630))
        val serverCache = server!!.getOrCreateCache<String, String>("one")
        val clientCache = client!!.getOrCreateCache<String, String>("one")
        var hasFired = false
        val entryListener = object : EntryListener<String, String> {
            override fun entryUpdated(entry: Pair<String, String>) {
                hasFired = true
            }

            override fun entryRemoved(entry: Pair<String, String>) {
            }
        }
        clientCache.addEntryListener(entryListener)
        serverCache.put("test", "value")
        Thread.sleep(500)
        Assertions.assertTrue(hasFired)
    }

    @Test
    fun filterTest() {
        this.server = this.factory.createServer(1630, emptyList())
        val serverCache = server!!.getOrCreateCache<String, Int>("one")
        serverCache.put("a", 1)
        serverCache.put("a4", 2)
        serverCache.put("a3", 3)
        serverCache.put("b", 4)
        serverCache.put("b1", 5)
        val values = serverCache.distributedQuery { it.first.contains("a") }
        Assertions.assertEquals(hashSetOf(1, 2, 3), values.toHashSet())
    }

    @Test
    fun filterTest2() {
        this.server = this.factory.createServer(1630, emptyList())
        val serverCache = server!!.getOrCreateCache<String, Int>("one")
        serverCache.put("a", 1)
        serverCache.put("a4", 2)
        serverCache.put("a3", 3)
        serverCache.put("b", 4)
        serverCache.put("b1", 5)
        val values = serverCache.distributedQuery { it.first.contains("b") }
        Assertions.assertEquals(hashSetOf(4, 5), values.toHashSet())
    }

    @Test
    fun itemsOnServerGetsAdded_FilterOnClient() {
        this.server = this.factory.createServer(1630, emptyList())
        this.client = this.factory.createClient(Address("127.0.0.1", 1630))
        val serverCache = server!!.getOrCreateCache<String, Int>("one")
        val clientCache = client!!.getOrCreateCache<String, Int>("one")
        serverCache.put("a", 1)
        serverCache.put("a4", 2)
        serverCache.put("a3", 3)
        serverCache.put("b", 4)
        serverCache.put("b1", 5)
        val values = clientCache.distributedQuery { it.first.contains("a") }
        Assertions.assertEquals(hashSetOf(1, 2, 3), values.toHashSet())
    }

}