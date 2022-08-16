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

package app.simplecloud.simplecloud.plugin.proxy.type.bungee

import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.process.group.ProcessTemplateType
import app.simplecloud.simplecloud.plugin.proxy.ProxyServerRegistry
import net.md_5.bungee.api.ProxyServer
import java.net.InetSocketAddress
import java.util.*

/**
 * Date: 24.01.22
 * Time: 19:01
 * @author Frederick Baier
 *
 */
class BungeeProxyServerRegistry(
    private val proxyServer: ProxyServer
) : ProxyServerRegistry {

    override fun registerProcess(cloudProcess: CloudProcess) {
        if (this.proxyServer.getServerInfo(cloudProcess.getName()) != null) {
            throw IllegalArgumentException("Service is already registered!")
        }

        if (cloudProcess.getProcessType() == ProcessTemplateType.PROXY)
            return

        println("Registered process ${cloudProcess.getName()}")
        val address = cloudProcess.getAddress()
        val socketAddress = InetSocketAddress(address.host, address.port)
        registerServer(cloudProcess.getName(), cloudProcess.getUniqueId(), socketAddress)
    }

    override fun registerServer(name: String, uniqueId: UUID, socketAddress: InetSocketAddress) {
        val info = ProxyServer.getInstance().constructServerInfo(
            name,
            socketAddress,
            uniqueId.toString(),
            false
        )
        ProxyServer.getInstance().servers[name] = info
    }

    override fun unregisterServer(name: String) {
        this.proxyServer.servers.remove(name)
    }
}