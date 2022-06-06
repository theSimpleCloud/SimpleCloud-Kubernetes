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

import app.simplecloud.simplecloud.api.process.CloudProcess
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Date: 29.05.22
 * Time: 14:14
 * @author Frederick Baier
 *
 */
class TestProxyServerRegistry : ProxyServerRegistry {

    private val registeredServers = CopyOnWriteArrayList<String>()

    override fun registerProcess(cloudProcess: CloudProcess) {
        this.registeredServers.add(cloudProcess.getName())
    }

    override fun registerServer(name: String, uniqueId: UUID, socketAddress: InetSocketAddress) {
        this.registeredServers.add(name)
    }

    override fun unregisterServer(name: String) {
        TODO("Not yet implemented")
    }
}