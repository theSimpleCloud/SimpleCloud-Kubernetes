package app.simplecloud.simplecloud.plugin.proxy.type.velocity

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.plugin.proxy.ProxyController
import app.simplecloud.simplecloud.plugin.proxy.request.ServerConnectedRequest
import app.simplecloud.simplecloud.plugin.proxy.request.ServerPreConnectRequest
import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.LoginEvent
import com.velocitypowered.api.event.connection.PostLoginEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import com.velocitypowered.api.event.player.ServerPostConnectEvent
import com.velocitypowered.api.event.player.ServerPreConnectEvent
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.Component

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 01.07.22
 * Time: 19:19
 */
class VelocityListener(
    private val proxyController: ProxyController,
    private val proxyServer: ProxyServer
) {

    @Subscribe(order = PostOrder.EARLY, async = true)
    fun handleJoin(event: LoginEvent) {
        if (!event.result.isAllowed) return
        val player = event.player
        val configuration = createConnectionConfiguration(player)
        CloudScope.launch {
            proxyController.handleLogin(configuration)
        }
    }

    @Subscribe(order = PostOrder.EARLY)
    fun handlePostLogin(event: PostLoginEvent) {
        val player = event.player
        val configuration = createConnectionConfiguration(player)
        CloudScope.launch {
            proxyController.handlePostLogin(configuration)
        }
    }

    @Subscribe(order = PostOrder.EARLY)
    fun handlePreConnect(event: ServerPreConnectEvent) {
        if (!event.result.isAllowed || !event.result.server.isPresent) return
        val player = event.player
        val configuration = createConnectionConfiguration(player)
        val currentServerName: String? = player.currentServer?.orElseGet(null)?.serverInfo?.name
        try {
            runBlocking {
                val response = proxyController.handleServerPreConnect(
                    ServerPreConnectRequest(
                        configuration,
                        currentServerName,
                        event.result.server.get().serverInfo.name
                    )
                )
                val serverInfo = proxyServer.getServer(response.targetProcessName).orElseGet(null) ?: return@runBlocking
                event.result = ServerPreConnectEvent.ServerResult.allowed(serverInfo)
            }
        } catch (ex: ProxyController.NoLobbyServerFoundException) {
            player.disconnect(Component.text("§cNo fallback server found"))
            event.result = ServerPreConnectEvent.ServerResult.denied()
        } catch (ex: ProxyController.NoSuchProcessException) {
            player.sendMessage(Component.text("§cProcess not found"))
            event.result = ServerPreConnectEvent.ServerResult.denied()
        } catch (ex: ProxyController.IllegalGroupTypeException) {
            player.sendMessage(Component.text("§cCannot connect to a proxy process"))
            event.result = ServerPreConnectEvent.ServerResult.denied()
        } catch (ex: ProxyController.ProcessNotJoinableException) {
            player.sendMessage(Component.text("§cProcess cannot be joined at the moment"))
            event.result = ServerPreConnectEvent.ServerResult.denied()
        } catch (ex: ProxyController.NoPermissionToJoinGroupException) {
            player.sendMessage(Component.text("§cYou don't have the permission to join this group"))
            event.result = ServerPreConnectEvent.ServerResult.denied()
        }

    }

    @Subscribe(order = PostOrder.EARLY)
    fun handleConnect(event: ServerConnectedEvent) {
        val player = event.player
        val configuration = createConnectionConfiguration(player)
        runBlocking {
            proxyController.handleServerConnected(ServerConnectedRequest(configuration, event.server.serverInfo.name))
        }
    }

    @Subscribe(order = PostOrder.LAST)
    fun handleDisconnect(event: DisconnectEvent) {
        val player = event.player
        CloudScope.launch {
            proxyController.handleDisconnect(player.uniqueId)
        }
    }

    private fun createConnectionConfiguration(connection: Player): PlayerConnectionConfiguration {
        val socketAddress = connection.remoteAddress
        return PlayerConnectionConfiguration(
            connection.uniqueId,
            connection.protocolVersion.protocol,
            connection.username,
            Address(socketAddress.hostString, socketAddress.port),
            connection.isOnlineMode
        )
    }

}
