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

package app.simplecloud.simplecloud.plugin.proxy

/**
 * Date: 24.01.22
 * Time: 19:40
 * @author Frederick Baier
 *
 */
/*
class ProxyIgniteUpdateListener @Inject constructor(
    private val proxyServerRegistry: ProxyServerRegistry,
    private val processFactory: CloudProcessFactory,
) : AbstractCacheEntryListener<String, CloudProcessConfiguration>(false, false) {

    override fun onCreated(events: MutableIterable<CacheEntryEvent<out String, out CloudProcessConfiguration>>) {
        events.forEach { handleCreateEvent(it) }
    }

    private fun handleCreateEvent(event: CacheEntryEvent<out String, out CloudProcessConfiguration>) {
        val cloudProcess = this.processFactory.create(event.value)
        this.proxyServerRegistry.registerProcess(cloudProcess)
    }

    override fun onRemoved(events: MutableIterable<CacheEntryEvent<out String, out CloudProcessConfiguration>>) {
        events.forEach { handleRemoveEvent(it) }
    }

    private fun handleRemoveEvent(event: CacheEntryEvent<out String, out CloudProcessConfiguration>) {
        this.proxyServerRegistry.unregisterServer(event.value.getProcessName())
    }

}

 */