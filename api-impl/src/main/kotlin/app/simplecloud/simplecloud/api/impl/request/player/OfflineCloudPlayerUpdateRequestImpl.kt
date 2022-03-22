package app.simplecloud.simplecloud.api.impl.request.player

import app.simplecloud.simplecloud.api.impl.request.permission.AbstractPermissionEntityUpdateRequest
import app.simplecloud.simplecloud.api.internal.service.InternalCloudPlayerService
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.PermissionPlayer
import app.simplecloud.simplecloud.api.permission.configuration.PermissionPlayerConfiguration
import app.simplecloud.simplecloud.api.player.OfflineCloudPlayer
import app.simplecloud.simplecloud.api.player.PlayerWebConfig
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
open class OfflineCloudPlayerUpdateRequestImpl(
    private val offlinePlayer: OfflineCloudPlayer,
    private val internalService: InternalCloudPlayerService,
    private val permissionFactory: Permission.Factory
) : AbstractPermissionEntityUpdateRequest(offlinePlayer, permissionFactory), OfflineCloudPlayerUpdateRequest {

    @Volatile
    protected var displayName: String = this.offlinePlayer.getDisplayName()

    @Volatile
    protected var webConfig: PlayerWebConfig = this.offlinePlayer.getWebConfig()

    override fun clearPermissions(): OfflineCloudPlayerUpdateRequest {
        super.clearPermissions()
        return this
    }

    override fun addPermission(permission: Permission): OfflineCloudPlayerUpdateRequest {
        super.addPermission(permission)
        return this
    }

    override fun removePermission(permissionString: String): OfflineCloudPlayerUpdateRequest {
        super.removePermission(permissionString)
        return this
    }

    override fun clearPermissionGroups(): OfflineCloudPlayerUpdateRequest {
        super.clearPermissionGroups()
        return this
    }

    override fun addPermissionGroup(permissionGroup: PermissionGroup, expiresAt: Long): OfflineCloudPlayerUpdateRequest {
        super.addPermissionGroup(permissionGroup, expiresAt)
        return this
    }

    override fun removePermissionGroup(groupName: String): OfflineCloudPlayerUpdateRequest {
        super.removePermissionGroup(groupName)
        return this
    }

    override fun getEntity(): PermissionPlayer {
        return this.offlinePlayer
    }

    override fun setDisplayName(name: String): OfflineCloudPlayerUpdateRequest {
        this.displayName = name
        return this
    }

    override fun setWebConfig(webConfig: PlayerWebConfig): OfflineCloudPlayerUpdateRequest {
        this.webConfig = webConfig
        return this
    }

    override fun submit(): CompletableFuture<Unit> {
        val lastPlayerConnection = this.offlinePlayer.getLastPlayerConnection()
        val connectionConfiguration = PlayerConnectionConfiguration(
            lastPlayerConnection.getUniqueId(),
            lastPlayerConnection.getVersion(),
            lastPlayerConnection.getName(),
            lastPlayerConnection.getAddress(),
            lastPlayerConnection.isOnlineMode()
        )
        val permissionPlayerConfiguration = PermissionPlayerConfiguration(
            this.offlinePlayer.getUniqueId(),
            this.permissions.map { it.toConfiguration() }
        )
        val offlineCloudPlayerConfiguration = OfflineCloudPlayerConfiguration(
            this.offlinePlayer.getName(),
            this.offlinePlayer.getUniqueId(),
            this.offlinePlayer.getFirstLogin(),
            this.offlinePlayer.getLastLogin(),
            this.offlinePlayer.getOnlineTime(),
            this.displayName,
            connectionConfiguration,
            this.webConfig,
            permissionPlayerConfiguration
        )
        return this.internalService.updateOfflinePlayerInternal(offlineCloudPlayerConfiguration)
    }
}