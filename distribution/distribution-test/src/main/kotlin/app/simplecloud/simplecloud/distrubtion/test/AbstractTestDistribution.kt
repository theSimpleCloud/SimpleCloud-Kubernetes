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

package app.simplecloud.simplecloud.distrubtion.test

import app.simplecloud.simplecloud.distribution.api.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Date: 08.04.22
 * Time: 17:42
 * @author Frederick Baier
 *
 */
abstract class AbstractTestDistribution : Distribution {

    protected val servers = CopyOnWriteArrayList<ServerComponent>()

    abstract val messageManager: TestMessageManager

    override fun getServers(): List<ServerComponent> {
        return this.servers
    }

    override fun getMessageManager(): MessageManager {
        return this.messageManager
    }

    override fun <K, V> getOrCreateCache(name: String): Cache<K, V> {
        return getVirtualCluster().getOrCreateCache(name)
    }

    open fun onComponentJoin(component: DistributionComponent) {
        if (component is ServerComponent) {
            if (!this.servers.contains(component))
                this.servers.add(component)
        }
    }

    abstract fun getVirtualCluster(): VirtualCluster

}