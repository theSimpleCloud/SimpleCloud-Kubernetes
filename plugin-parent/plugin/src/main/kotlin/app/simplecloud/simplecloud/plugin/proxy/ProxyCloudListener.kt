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

import app.simplecloud.simplecloud.api.event.process.CloudProcessRegisteredEvent
import app.simplecloud.simplecloud.api.event.process.CloudProcessUnregisteredEvent
import app.simplecloud.simplecloud.eventapi.CloudEventHandler
import app.simplecloud.simplecloud.eventapi.Listener

/**
 * Date: 08.05.22
 * Time: 18:46
 * @author Frederick Baier
 *
 */
class ProxyCloudListener(
    private val proxyServerRegistry: ProxyServerRegistry
) : Listener {

    @CloudEventHandler
    fun onProcessRegistered(event: CloudProcessRegisteredEvent) {
        this.proxyServerRegistry.registerProcess(event.process)
    }

    @CloudEventHandler
    fun onProcessUnregistered(event: CloudProcessUnregisteredEvent) {
        this.proxyServerRegistry.unregisterServer(event.process.getName())
    }

}