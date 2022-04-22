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

import app.simplecloud.simplecloud.distibution.hazelcast.HazelcastDistributionFactory
import app.simplecloud.simplecloud.plugin.proxy.ProxyBinderModule
import app.simplecloud.simplecloud.plugin.proxy.ProxyProcessRegisterer
import app.simplecloud.simplecloud.plugin.proxy.ProxyServerRegistry
import app.simplecloud.simplecloud.plugin.proxy.type.bungee.guice.BungeeBinderModule
import app.simplecloud.simplecloud.plugin.startup.CloudPlugin
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin
import java.net.InetSocketAddress
import java.util.*

class CloudBungeePlugin : Plugin() {

    private val cloudPlugin = CloudPlugin(ProxyBinderModule(BungeeBinderModule(this)), HazelcastDistributionFactory())
    private val injector = cloudPlugin.injector

    override fun onLoad() {
        ProxyServer.getInstance().reconnectHandler = ReconnectHandlerImpl()
    }

    override fun onEnable() {
        val proxyServer = ProxyServer.getInstance()
        proxyServer.configurationAdapter.servers.clear()
        proxyServer.servers.clear()
        for (info in proxyServer.configurationAdapter.listeners) {
            info.serverPriority.clear()
        }
        registerFallbackService()

        val proxyProcessRegisterer = this.injector.getInstance(ProxyProcessRegisterer::class.java)
        proxyProcessRegisterer.registerExistingProcesses()
        proxyProcessRegisterer.registerEntryListener()

        proxyServer.pluginManager.registerListener(this, this.injector.getInstance(BungeeListener::class.java))
    }

    private fun registerFallbackService() {
        val proxyServerRegistry = this.injector.getInstance(ProxyServerRegistry::class.java)
        proxyServerRegistry.registerServer(
            "fallback",
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            InetSocketAddress("127.0.0.1", 0)
        )
    }

    override fun onDisable() {

    }

}