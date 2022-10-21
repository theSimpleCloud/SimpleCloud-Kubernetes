package app.simplecloud.simplecloud.plugin.proxy.type.velocity

import app.simplecloud.simplecloud.api.player.message.ActionBarConfiguration
import app.simplecloud.simplecloud.api.player.message.MessageConfiguration
import app.simplecloud.simplecloud.plugin.proxy.CloudPlayerProxyActions
import com.velocitypowered.api.proxy.ProxyServer

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 20.10.22
 * Time: 21:27
 */
class CloudPlayerVelocityActions(
    private val proxyServer: ProxyServer
): CloudPlayerProxyActions {

    override fun sendMessage(configuration: MessageConfiguration) {
        val player = this.proxyServer.getPlayer(configuration.uniqueId).orElseGet(null)?: return
        player.sendMessage(configuration.message, configuration.type)
    }

    override fun sendActionBar(configuration: ActionBarConfiguration) {
        val player = this.proxyServer.getPlayer(configuration.uniqueId).orElseGet(null)?: return
        player.sendActionBar(configuration.message)
    }

}