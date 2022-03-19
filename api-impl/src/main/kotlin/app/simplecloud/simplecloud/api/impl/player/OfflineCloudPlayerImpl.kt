package app.simplecloud.simplecloud.api.impl.player

import app.simplecloud.simplecloud.api.impl.request.player.OfflineCloudPlayerUpdateRequestImpl
import app.simplecloud.simplecloud.api.internal.service.InternalCloudPlayerService
import app.simplecloud.simplecloud.api.player.OfflineCloudPlayer
import app.simplecloud.simplecloud.api.player.PlayerConnection
import app.simplecloud.simplecloud.api.player.PlayerWebConfig
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import app.simplecloud.simplecloud.api.request.player.OfflineCloudPlayerUpdateRequest
import app.simplecloud.simplecloud.permission.entity.PermissionEntityImpl
import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import java.util.*

/**
 * Date: 07.01.22
 * Time: 17:57
 * @author Frederick Baier
 *
 */
open class OfflineCloudPlayerImpl @Inject constructor(
    @Assisted private val configuration: OfflineCloudPlayerConfiguration,
    private val cloudPlayerService: InternalCloudPlayerService
) : PermissionEntityImpl(), OfflineCloudPlayer {

    private val lastPlayerConnection = PlayerConnectionImpl(this.configuration.lastPlayerConnection)

    override fun getUniqueId(): UUID {
        return this.configuration.uniqueId
    }

    override fun getFirstLogin(): Long {
        return this.configuration.firstLogin
    }

    override fun getLastLogin(): Long {
        return this.configuration.lastLogin
    }

    override fun getOnlineTime(): Long {
        return this.configuration.onlineTime
    }

    override fun getLastPlayerConnection(): PlayerConnection {
        return this.lastPlayerConnection
    }

    override fun isOnline(): Boolean {
        return false
    }

    override fun getDisplayName(): String {
        return this.configuration.displayName
    }

    override fun getWebConfig(): PlayerWebConfig {
        return this.configuration.webConfig
    }


    override fun toOfflinePlayer(): OfflineCloudPlayer {
        return this
    }

    override fun toConfiguration(): OfflineCloudPlayerConfiguration {
        return this.configuration
    }

    override fun createUpdateRequest(): OfflineCloudPlayerUpdateRequest {
        return OfflineCloudPlayerUpdateRequestImpl(this, this.cloudPlayerService)
    }

    override fun getName(): String {
        return this.configuration.name
    }
}