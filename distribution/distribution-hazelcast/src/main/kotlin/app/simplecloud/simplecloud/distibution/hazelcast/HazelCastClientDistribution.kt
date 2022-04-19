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

package app.simplecloud.simplecloud.distibution.hazelcast

import app.simplecloud.simplecloud.api.utils.Address
import app.simplecloud.simplecloud.distribution.api.ClientComponent
import app.simplecloud.simplecloud.distribution.api.NetworkComponent
import app.simplecloud.simplecloud.distribution.api.impl.ClientComponentImpl
import com.hazelcast.client.HazelcastClient
import com.hazelcast.client.config.ClientConfig
import com.hazelcast.client.config.ClientConnectionStrategyConfig
import com.hazelcast.core.HazelcastInstance
import java.net.ConnectException

class HazelCastClientDistribution(
    private val connectAddress: Address
) : AbstractHazelCastDistribution() {

    private val hazelCast: HazelcastInstance = createHazelCastInstance()

    private val selfComponent = ClientComponentImpl(this.hazelCast.localEndpoint.uuid)

    private fun createHazelCastInstance(): HazelcastInstance {
        val config = ClientConfig()
        config.networkConfig.addAddress(connectAddress.asIpString())
        config.connectionStrategyConfig.reconnectMode = ClientConnectionStrategyConfig.ReconnectMode.OFF
        config.connectionStrategyConfig.connectionRetryConfig.clusterConnectTimeoutMillis = 1
        val hazelcastClient = try {
            HazelcastClient.newHazelcastClient(config)
        } catch (e: IllegalStateException) {
            throw ConnectException()
        }
        return hazelcastClient
    }

    override fun getHazelCastInstance(): HazelcastInstance {
        return this.hazelCast
    }

    override fun getSelfComponent(): NetworkComponent {
        return this.selfComponent
    }

    override fun getConnectedClients(): List<ClientComponent> {
        return emptyList()
    }

    override fun shutdown() {
        this.hazelCast.shutdown()
    }

}
