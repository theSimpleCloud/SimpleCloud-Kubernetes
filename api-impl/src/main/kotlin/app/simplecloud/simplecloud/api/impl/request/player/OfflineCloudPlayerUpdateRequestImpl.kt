package app.simplecloud.simplecloud.api.impl.request.player

import app.simplecloud.simplecloud.api.internal.service.InternalCloudPlayerService
import app.simplecloud.simplecloud.api.player.OfflineCloudPlayer
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.api.request.player.OfflineCloudPlayerUpdateRequest
import java.util.concurrent.CompletableFuture

/**
 * Date: 07.01.22
 * Time: 18:05
 * @author Frederick Baier
 *
 */
class OfflineCloudPlayerUpdateRequestImpl(
    private val offlinePlayer: OfflineCloudPlayer,
    private val internalService: InternalCloudPlayerService
) : OfflineCloudPlayerUpdateRequest {

    @Volatile
    private var displayName: String = this.offlinePlayer.getDisplayName()

    override fun setDisplayName(name: String) {
        this.displayName = name
    }

    override fun submit(): CompletableFuture<Unit> {
        val lastPlayerConnection = offlinePlayer.getLastPlayerConnection()
        val connectionConfiguration = PlayerConnectionConfiguration(
            lastPlayerConnection.getUniqueId(),
            lastPlayerConnection.getVersion(),
            lastPlayerConnection.getName(),
            lastPlayerConnection.getAddress(),
            lastPlayerConnection.isOnlineMode()
        )
        val offlineCloudPlayerConfiguration = OfflineCloudPlayerConfiguration(
            this.offlinePlayer.getName(),
            this.offlinePlayer.getUniqueId(),
            this.offlinePlayer.getFirstLogin(),
            this.offlinePlayer.getLastLogin(),
            this.offlinePlayer.getOnlineTime(),
            this.displayName,
            connectionConfiguration
        )
        return this.internalService.updateOfflinePlayerInternal(offlineCloudPlayerConfiguration)
    }
}