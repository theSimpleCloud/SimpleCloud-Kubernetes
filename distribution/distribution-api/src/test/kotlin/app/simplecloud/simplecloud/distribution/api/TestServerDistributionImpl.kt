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

import app.simplecloud.simplecloud.distribution.api.impl.ServerComponentImpl
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class TestServerDistributionImpl(
    val port: Int,
    private val addresses: List<Address>
) : AbstractTestDistribution() {

    private val selfComponent = ServerComponentImpl(UUID.randomUUID())

    private val connectedClients = CopyOnWriteArrayList<ClientComponent>()

    private val virtualCluster = VirtualNetwork.registerServer(this, addresses.map { it.port })

    override val messageManager: TestMessageManager = TestMessageManager(this.selfComponent, this.virtualCluster)

    init {
        this.servers.add(this.selfComponent)
    }

    override fun getSelfComponent(): NetworkComponent {
        return this.selfComponent
    }

    override fun getConnectedClients(): List<ClientComponent> {
        return this.connectedClients
    }

    override fun getVirtualCluster(): VirtualCluster {
        return this.virtualCluster
    }

    override fun shutdown() {

    }

    override fun onComponentJoin(component: NetworkComponent) {
        super.onComponentJoin(component)
        if (component is ClientComponent)
            this.connectedClients.add(component)
    }

}
