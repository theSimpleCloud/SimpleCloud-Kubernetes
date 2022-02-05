package app.simplecloud.simplecloud.plugin.proxy.type.bungee

import app.simplecloud.simplecloud.plugin.OnlineCountProvider
import com.google.inject.Inject
import com.google.inject.Singleton
import net.md_5.bungee.api.ProxyServer

/**
 * Date: 23.01.22
 * Time: 19:13
 * @author Frederick Baier
 *
 */
@Singleton
class BungeeOnlineCountProvider @Inject constructor(
    private val proxyServer: ProxyServer
) : OnlineCountProvider {

    override fun getOnlineCount(): Int {
        return proxyServer.onlineCount
    }

}