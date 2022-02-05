package app.simplecloud.simplecloud.plugin.proxy.type.bungee.guice

import app.simplecloud.simplecloud.plugin.OnlineCountProvider
import app.simplecloud.simplecloud.plugin.proxy.ProxyServerRegistry
import app.simplecloud.simplecloud.plugin.proxy.type.bungee.BungeeOnlineCountProvider
import app.simplecloud.simplecloud.plugin.proxy.type.bungee.BungeeProxyServerRegistry
import com.google.inject.AbstractModule
import net.md_5.bungee.api.ProxyServer

/**
 * Date: 24.01.22
 * Time: 19:04
 * @author Frederick Baier
 *
 */
class BungeeBinderModule : AbstractModule() {

    override fun configure() {
        bind(ProxyServer::class.java).toInstance(ProxyServer.getInstance())
        bind(ProxyServerRegistry::class.java).to(BungeeProxyServerRegistry::class.java)
        bind(OnlineCountProvider::class.java).to(BungeeOnlineCountProvider::class.java)
    }

}