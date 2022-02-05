package app.simplecloud.simplecloud.plugin.proxy.type.bungee

import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.api.utils.Address
import app.simplecloud.simplecloud.plugin.proxy.ProxyController
import com.google.inject.Inject
import net.md_5.bungee.api.connection.PendingConnection
import net.md_5.bungee.api.event.LoginEvent
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.event.EventPriority
import java.net.InetSocketAddress

/**
 * Date: 14.01.22
 * Time: 14:34
 * @author Frederick Baier
 *
 */
class BungeeListener @Inject constructor(
    private val proxyController: ProxyController,
) : Listener {

    @EventHandler(priority = EventPriority.LOW)
    fun handleJoin(event: LoginEvent) {
        if (event.isCancelled) return
        println("handle login")
        val connection = event.connection
        val configuration = createConnectionConfiguration(connection)
        this.proxyController.handleLogin(configuration)
    }

    @EventHandler
    fun on(event: PostLoginEvent) {
        val proxiedPlayer = event.player
        proxiedPlayer.reconnectServer = null
        val configuration = createConnectionConfiguration(proxiedPlayer.pendingConnection)
        this.proxyController.handlePostLogin(configuration)
    }

    private fun createConnectionConfiguration(connection: PendingConnection): PlayerConnectionConfiguration {
        val socketAddress = connection.socketAddress
        socketAddress as InetSocketAddress
        return PlayerConnectionConfiguration(
            connection.uniqueId,
            connection.version,
            connection.name,
            Address(socketAddress.hostString, socketAddress.port),
            connection.isOnlineMode
        )
    }

}