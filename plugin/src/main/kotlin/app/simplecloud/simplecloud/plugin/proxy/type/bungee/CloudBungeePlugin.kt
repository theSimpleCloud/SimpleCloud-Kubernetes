/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package app.simplecloud.simplecloud.plugin.proxy.type.bungee

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

    private val cloudPlugin = CloudPlugin(ProxyBinderModule(BungeeBinderModule()))
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
        proxyProcessRegisterer.registerIgniteListener()

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