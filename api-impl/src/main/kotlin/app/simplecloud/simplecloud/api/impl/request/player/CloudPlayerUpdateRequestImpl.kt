package app.simplecloud.simplecloud.api.impl.request.player

import app.simplecloud.simplecloud.api.internal.request.player.InternalCloudPlayerUpdateRequest
import app.simplecloud.simplecloud.api.internal.service.InternalCloudPlayerService
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.player.configuration.CloudPlayerConfiguration
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import java.util.concurrent.CompletableFuture

/**
 * Date: 13.01.22
 * Time: 18:53
 * @author Frederick Baier
 *
 */
class CloudPlayerUpdateRequestImpl(
    private val cloudPlayer: CloudPlayer,
    private val internalService: InternalCloudPlayerService
): InternalCloudPlayerUpdateRequest {

    @Volatile
    private var displayName: String = this.cloudPlayer.getDisplayName()

    @Volatile
    private var connectedProxyName: String = this.cloudPlayer.getCurrentProxyName()

    @Volatile
    private var connectedServerName: String? = this.cloudPlayer.getCurrentServerName()

    override fun setDisplayName(name: String) {
        this.displayName = name
    }

    override fun setConnectedProxyName(name: String): InternalCloudPlayerUpdateRequest {
        this.connectedProxyName = name
        return this
    }

    override fun setConnectedServerName(name: String): InternalCloudPlayerUpdateRequest {
        this.connectedServerName = name
        return this
    }

    override fun submit(): CompletableFuture<Unit> {
        val playerConnection = cloudPlayer.getPlayerConnection()
        val connectionConfiguration = PlayerConnectionConfiguration(
            playerConnection.getUniqueId(),
            playerConnection.getVersion(),
            playerConnection.getName(),
            playerConnection.getAddress(),
            playerConnection.isOnlineMode()
        )
        val cloudPlayerConfiguration = CloudPlayerConfiguration(
            this.cloudPlayer.getName(),
            this.cloudPlayer.getUniqueId(),
            this.cloudPlayer.getFirstLogin(),
            this.cloudPlayer.getLastLogin(),
            this.cloudPlayer.getOnlineTime(),
            connectionConfiguration,
            this.displayName,
            this.connectedServerName,
            this.connectedProxyName
        )
        return this.internalService.updateOnlinePlayerInternal(cloudPlayerConfiguration)
    }
}