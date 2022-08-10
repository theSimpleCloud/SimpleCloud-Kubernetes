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

import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.distribution.api.ClientComponent
import app.simplecloud.simplecloud.distribution.api.DistributionComponent
import app.simplecloud.simplecloud.distribution.api.ServerComponent
import app.simplecloud.simplecloud.distribution.api.impl.ServerComponentImpl
import java.util.*

class TestServerDistributionImpl(
    val port: Int,
    private val addresses: List<Address>
) : AbstractTestDistribution() {

    private val selfComponent = ServerComponentImpl(UUID.randomUUID())

    private val virtualCluster = VirtualNetwork.registerServer(this, addresses.map { it.port })

    override val messageManager: TestMessageManager = TestMessageManager(this.selfComponent, this.virtualCluster)

    override fun getSelfComponent(): ServerComponent {
        return this.selfComponent
    }

    override fun getConnectedClients(): List<ClientComponent> {
        return this.virtualCluster.getClientComponents()
    }

    override fun getVirtualCluster(): VirtualCluster {
        return this.virtualCluster
    }

    override fun onComponentJoin(component: DistributionComponent) {

    }

}
