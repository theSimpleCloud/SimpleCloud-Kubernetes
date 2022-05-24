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

import app.simplecloud.simplecloud.api.impl.env.RealEnvironmentVariables
import app.simplecloud.simplecloud.distibution.hazelcast.HazelcastDistributionFactory
import app.simplecloud.simplecloud.eventapi.EventRegisterer
import app.simplecloud.simplecloud.plugin.proxy.ProxyCloudListener
import app.simplecloud.simplecloud.plugin.proxy.ProxyControllerImpl
import app.simplecloud.simplecloud.plugin.proxy.ProxyProcessRegisterer
import app.simplecloud.simplecloud.plugin.startup.CloudPlugin
import app.simplecloud.simplecloud.plugin.startup.SelfProcessProvider
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin
import java.net.InetSocketAddress
import java.util.*

class CloudBungeePlugin : Plugin() {

    private val cloudPlugin = CloudPlugin(HazelcastDistributionFactory(), RealEnvironmentVariables())
    private val cloudAPI = cloudPlugin.pluginCloudAPI
    private val proxyServerRegistry = BungeeProxyServerRegistry(ProxyServer.getInstance())

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

        val proxyProcessRegisterer = ProxyProcessRegisterer(this.cloudAPI.getProcessService(), this.proxyServerRegistry)
        proxyProcessRegisterer.registerExistingProcesses()

        this.cloudAPI.getEventManager().registerListener(
            object : EventRegisterer {},
            ProxyCloudListener(this.proxyServerRegistry)
        )

        val proxyController = ProxyControllerImpl(
            this.cloudAPI.internalPlayerService,
            this.cloudAPI.getProcessService(),
            this.cloudAPI.getProcessGroupService(),
            BungeeOnlineCountUpdater(
                proxyServer,
                SelfProcessProvider(this.cloudPlugin.selfProcessId, this.cloudAPI.getProcessService())
            ),
        )
        proxyServer.pluginManager.registerListener(
            this, BungeeListener(
                this.cloudAPI.getLocalNetworkComponentName(),
                proxyController,
                this,
                proxyServer
            )
        )
    }

    private fun registerFallbackService() {
        this.proxyServerRegistry.registerServer(
            "fallback",
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            InetSocketAddress("127.0.0.1", 0)
        )
    }

    override fun onDisable() {

    }

}