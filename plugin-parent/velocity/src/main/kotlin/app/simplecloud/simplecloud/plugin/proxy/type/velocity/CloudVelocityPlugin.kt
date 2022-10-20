package app.simplecloud.simplecloud.plugin.proxy.type.velocity

import app.simplecloud.simplecloud.api.impl.env.RealEnvironmentVariables
import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.distribution.hazelcast.HazelcastDistributionFactory
import app.simplecloud.simplecloud.plugin.SelfOnlineCountProvider
import app.simplecloud.simplecloud.plugin.proxy.CloudProxyPlugin
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import java.net.InetSocketAddress
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 01.07.22
 * Time: 19:08
 */
@Plugin(
    id = "simplecloud_plugin",
    name = "SimpleCloud-Plugin",
    version = "1.0",
    authors = ["Fllip"]
)
class CloudVelocityPlugin @Inject constructor(
    private val proxyServer: ProxyServer
) {

    private val proxyServerRegistry = VelocityProxyServerRegistry(this.proxyServer)
    private val proxyPlugin = CloudProxyPlugin(
        HazelcastDistributionFactory(),
        RealEnvironmentVariables(),
        Address.fromIpString("distribution:1670"),
        this.proxyServerRegistry,
        SelfOnlineCountProvider { this.proxyServer.allPlayers.size },
        CloudPlayerVelocityActions(this.proxyServer)
    )

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        this.proxyServer.allServers.forEach {
            this.proxyServer.unregisterServer(it.serverInfo)
        }

        this.registerFallbackService()

        this.proxyServer.eventManager.register(this, VelocityListener(this.proxyPlugin.proxyController, this.proxyServer))

    }

    private fun registerFallbackService() {
        this.proxyServerRegistry.registerServer(
            "fallback",
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            InetSocketAddress("127.0.0.1", 0)
        )
    }

}