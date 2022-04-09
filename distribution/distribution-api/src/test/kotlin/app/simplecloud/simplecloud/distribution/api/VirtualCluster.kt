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

package app.simplecloud.simplecloud.distribution.api

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

    init {
        this.servers.add(initialServer)
    }

    fun addServer(server: TestServerDistributionImpl) {
        this.servers.add(server)
        onDistributionJoin(server)
    }

    fun addClient(client: TestClientDistributionImpl) {
        this.clients.add(client)
        onDistributionJoin(client)
    }

    private fun onDistributionJoin(joiningDistribution: AbstractTestDistribution) {
        val allDistributions = getAllDistributions()
        allDistributions.forEach { it.onMemberJoin(joiningDistribution.getSelfMember()) }
        allDistributions.forEach { joiningDistribution.onMemberJoin(it.getSelfMember()) }
    }

    fun getAllDistributions(): Set<AbstractTestDistribution> {
        return this.servers.union(this.clients)
    }

    fun getServerPorts(): List<Int> {
        return this.servers.map { it.port }
    }

    fun sendMessage(message: Any) {
        this.getAllDistributions().forEach { it.messageManager.onReceive(message) }
    }

    fun sendMessage(message: Any, receiver: Member) {
        val receiverDistribution = getAllDistributions().firstOrNull { it.getSelfMember() == receiver }
        receiverDistribution?.messageManager?.onReceive(message)
    }

    fun <K, V> getOrCreateCache(name: String): Cache<K, V> {
        if (this.cacheNameToCache.containsKey(name)) {
            return this.cacheNameToCache[name] as Cache<K, V>
        }
        val cache = TestCacheImpl<K, V>()
        this.cacheNameToCache[name] = cache
        return cache
    }


}