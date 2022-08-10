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

package app.simplecloud.simplecloud.distribution.test

import app.simplecloud.simplecloud.distribution.api.*
import app.simplecloud.simplecloud.distribution.test.scheduler.TestThreadScheduledExecutorService
import com.google.common.collect.Maps
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Date: 08.04.22
 * Time: 18:19
 * @author Frederick Baier
 *
 */
class VirtualCluster(
    initialServer: TestServerDistributionImpl
) {

    private val servers = CopyOnWriteArrayList<TestServerDistributionImpl>()
    private val clients = CopyOnWriteArrayList<TestClientDistributionImpl>()

    private val cacheNameToCache = Maps.newConcurrentMap<String, Cache<*, *>>()

    private val nameToScheduler = Maps.newConcurrentMap<String, ScheduledExecutorService>()

    init {
        this.servers.add(initialServer)
    }

    fun getServerComponents(): List<ServerComponent> {
        return this.servers.map { it.getSelfComponent() }
    }

    fun getClientComponents(): List<ClientComponent> {
        return this.clients.map { it.getSelfComponent() }
    }

    fun addServer(server: TestServerDistributionImpl) {
        this.servers.add(server)
        onServerJoin(server)
    }

    fun addClient(client: TestClientDistributionImpl) {
        this.clients.add(client)
        this.servers.forEach {
            client.onComponentJoin(it.getSelfComponent())
            it.onComponentJoin(client.getSelfComponent())
        }
    }

    fun removeComponent(component: AbstractTestDistribution) {
        if (component is TestClientDistributionImpl) {
            removeClient(component)
        } else {
            removeServer(component as TestServerDistributionImpl)
        }
    }

    private fun removeServer(component: TestServerDistributionImpl) {
        getAllDistributions().forEach {
            component.onComponentLeave(it.getSelfComponent())
            it.onComponentLeave(component.getSelfComponent())
        }
        this.servers.remove(component)
        if (this.servers.isEmpty()) {
            shutdownCluster()
        }
    }

    private fun shutdownCluster() {
        this.clients.forEach { it.shutdown() }
        this.nameToScheduler.values.forEach { it.shutdown() }
    }

    private fun removeClient(component: TestClientDistributionImpl) {
        this.servers.forEach {
            component.onComponentLeave(it.getSelfComponent())
            it.onComponentLeave(component.getSelfComponent())
        }
        this.clients.remove(component)
    }

    private fun onServerJoin(joiningDistribution: AbstractTestDistribution) {
        getAllDistributions().forEach { it.onComponentJoin(joiningDistribution.getSelfComponent()) }
    }

    fun getServerPorts(): List<Int> {
        return this.servers.map { it.port }
    }

    fun sendMessage(sender: DistributionComponent, message: Any) {
        this.getAllDistributions().forEach { it.messageManager.onReceive(message, sender) }
    }

    private fun getAllDistributions(): Set<AbstractTestDistribution> {
        return this.servers.union(this.clients)
    }

    fun sendMessage(sender: DistributionComponent, message: Any, receiver: DistributionComponent) {
        val receiverDistribution = getAllDistributions().firstOrNull { it.getSelfComponent() == receiver }
        receiverDistribution?.messageManager?.onReceive(message, sender)
    }

    fun <K, V> getOrCreateCache(name: String): Cache<K, V> {
        if (this.cacheNameToCache.containsKey(name)) {
            return this.cacheNameToCache[name] as Cache<K, V>
        }
        val cache = TestCacheImpl<K, V>(name)
        this.cacheNameToCache[name] = cache
        return cache
    }

    fun getServerByPort(port: Int): TestServerDistributionImpl? {
        return this.servers.firstOrNull { it.port == port }
    }

    fun getScheduler(name: String): ScheduledExecutorService {
        if (this.nameToScheduler.containsKey(name)) {
            return this.nameToScheduler[name]!!
        }
        val scheduler = TestThreadScheduledExecutorService()
        this.nameToScheduler[name] = scheduler
        return scheduler
    }


}