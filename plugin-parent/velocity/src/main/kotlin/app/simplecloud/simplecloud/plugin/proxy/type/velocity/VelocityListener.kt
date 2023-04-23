package app.simplecloud.simplecloud.plugin.proxy.type.velocity

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.plugin.proxy.ProxyController
import app.simplecloud.simplecloud.plugin.proxy.request.ServerConnectedRequest
import app.simplecloud.simplecloud.plugin.proxy.request.ServerKickRequest
import app.simplecloud.simplecloud.plugin.proxy.request.ServerPreConnectRequest
import com.velocitypowered.api.event.Continuation
import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.LoginEvent
import com.velocitypowered.api.event.connection.PostLoginEvent
import com.velocitypowered.api.event.player.KickedFromServerEvent
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import com.velocitypowered.api.event.player.ServerPreConnectEvent
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent

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

    @Subscribe(order = PostOrder.EARLY)
    fun handleJoin(event: LoginEvent, continuation: Continuation) {
        if (!event.result.isAllowed) return
        val player = event.player
        val configuration = createConnectionConfiguration(player)
        val loginJob = CloudScope.launch {
            proxyController.handleLogin(configuration)
        }
        handJobCompletionToContinuation(loginJob, continuation)
    }

    @Subscribe(order = PostOrder.EARLY)
    fun handlePostLogin(event: PostLoginEvent, continuation: Continuation) {
        val player = event.player
        val configuration = createConnectionConfiguration(player)
        val postLoginJob = CloudScope.launch {
            proxyController.handlePostLogin(configuration)
        }
        handJobCompletionToContinuation(postLoginJob, continuation)
    }

    @Subscribe(order = PostOrder.EARLY)
    fun handleChooseInitialServer(event: PlayerChooseInitialServerEvent) {
        event.setInitialServer(this.proxyServer.getServer("fallback").get())
    }

    @Subscribe(order = PostOrder.EARLY)
    fun handlePreConnect(event: ServerPreConnectEvent) {
        if (!event.result.isAllowed || !event.result.server.isPresent) return
        val player = event.player
        val configuration = createConnectionConfiguration(player)
        val currentServerName: String? = player.currentServer?.orElse(null)?.serverInfo?.name
        try {
            runBlocking {
                val response = proxyController.handleServerPreConnect(
                    ServerPreConnectRequest(
                        configuration,
                        currentServerName,
                        event.result.server.get().serverInfo.name
                    )
                )
                val serverInfo = proxyServer.getServer(response.targetProcessName).orElse(null)
                if (serverInfo == null) {
                    player.disconnect(Component.text("§cTarget process ${response.targetProcessName} is not registered"))
                    event.result = ServerPreConnectEvent.ServerResult.denied()
                    return@runBlocking
                }
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

    @Subscribe(order = PostOrder.LAST)
    fun handleKick(event: KickedFromServerEvent) {
        val player = event.player

        val kickReasonString: String = getKickMessage(event)
        val kickRequest = ServerKickRequest(player.uniqueId, kickReasonString, event.server.serverInfo.name)
        runBlocking {
            try {
                val response = proxyController.handleServerKick(kickRequest)
                val serverInfo = proxyServer.getServer(response.targetProcessName).orElse(null) ?: return@runBlocking
                event.result = KickedFromServerEvent.RedirectPlayer.create(serverInfo)
            } catch (ex: ProxyController.NoLobbyServerFoundException) {
                event.result =
                    KickedFromServerEvent.DisconnectPlayer.create(Component.text("§cNo fallback server found"))
            }
        }
    }

    private fun getKickMessage(
        event: KickedFromServerEvent
    ): String {
        val kickReasonComponent = event.serverKickReason
        return if (!kickReasonComponent.isPresent) {
            ""
        } else {
            val component = kickReasonComponent.get()
            if (component is TextComponent) {
                component.content()
            } else {
                "§cYou were kicked from the server"
            }
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

    private fun handJobCompletionToContinuation(job: Job, continuation: Continuation) {
        job.invokeOnCompletion { cause ->
            if (cause === null) continuation.resume()
            else continuation.resumeWithException(cause)
        }
    }

}
