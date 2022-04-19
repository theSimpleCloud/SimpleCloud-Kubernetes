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

import java.net.BindException
import java.net.ConnectException
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Date: 08.04.22
 * Time: 17:37
 * @author Frederick Baier
 *
 */
object VirtualNetwork {


    private val virtualClusters = CopyOnWriteArrayList<VirtualCluster>()

    fun registerServer(server: TestServerDistributionImpl, otherServerPorts: List<Int>): VirtualCluster {
        checkForPortInUse(server)
        val existingCluster = otherServerPorts.map { findVirtualClusterByPort(it) }.firstOrNull()
            ?: return createNewCluster(server)
        existingCluster.addServer(server)
        return existingCluster
    }

    private fun createNewCluster(server: TestServerDistributionImpl): VirtualCluster {
        val newCluster = VirtualCluster(server)
        this.virtualClusters.add(newCluster)
        return newCluster
    }

    private fun checkForPortInUse(server: TestServerDistributionImpl) {
        val clusterByServerPort = findVirtualClusterByPort(server.port)
        if (clusterByServerPort != null)
            throw BindException("Address already in use!")
    }

    fun connectClient(client: TestClientDistributionImpl, port: Int): VirtualCluster {
        val cluster = findVirtualClusterByPort(port) ?: throw ConnectException("No Server is running at port $port")
        val server = cluster.getServerByPort(port)!!
        cluster.addClient(client, server)
        return cluster
    }

    private fun findVirtualClusterByPort(port: Int): VirtualCluster? {
        return this.virtualClusters.firstOrNull { it.getServerPorts().contains(port) }
    }

    fun reset() {
        this.virtualClusters.clear()
    }

}