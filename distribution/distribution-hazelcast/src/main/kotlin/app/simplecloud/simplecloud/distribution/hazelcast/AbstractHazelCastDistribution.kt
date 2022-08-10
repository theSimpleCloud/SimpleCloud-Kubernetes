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

package app.simplecloud.simplecloud.distribution.hazelcast

import app.simplecloud.simplecloud.distribution.api.*
import app.simplecloud.simplecloud.distribution.api.impl.ServerComponentImpl
import com.hazelcast.core.HazelcastInstance

/**
 * Date: 10.04.22
 * Time: 18:19
 * @author Frederick Baier
 *
 */
abstract class AbstractHazelCastDistribution : Distribution {

    abstract fun getHazelCastInstance(): HazelcastInstance

    override fun getServers(): List<ServerComponent> {
        return getHazelCastInstance().cluster.members.map { ServerComponentImpl(it.uuid) }
    }

    override fun <K, V> getOrCreateCache(name: String): Cache<K, V> {
        return HazelCastCache(getHazelCastInstance().getMap(name))
    }

    override fun getMessageManager(): MessageManager {
        return HazelCastMessageManager(getSelfComponent(), getHazelCastInstance())
    }

    override fun getScheduler(name: String): ScheduledExecutorService {
        TODO()
    }

}