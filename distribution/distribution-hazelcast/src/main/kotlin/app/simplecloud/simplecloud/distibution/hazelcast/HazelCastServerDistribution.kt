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
import app.simplecloud.simplecloud.distribution.api.Member
import app.simplecloud.simplecloud.distribution.api.SimpleMemberImpl
import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance

class HazelCastServerDistribution(
    private val bindPort: Int,
    private val connectAddresses: List<Address>
) : AbstractHazelCastDistribution() {

    private val hazelCast: HazelcastInstance = createHazelCastInstance()

    private val selfMember: Member = SimpleMemberImpl(this.hazelCast.cluster.localMember.uuid)


    private fun createHazelCastInstance(): HazelcastInstance {
        val config = Config()
        config.networkConfig.port = this.bindPort
        for (address in connectAddresses) {
            config.networkConfig.join.tcpIpConfig.addMember(address.asIpString())
        }
        return Hazelcast.newHazelcastInstance(config)
    }

    override fun getHazelCastInstance(): HazelcastInstance {
        return this.hazelCast
    }

    override fun getSelfMember(): Member {
        return this.selfMember
    }

    override fun shutdown() {
        this.hazelCast.shutdown()
    }

}
