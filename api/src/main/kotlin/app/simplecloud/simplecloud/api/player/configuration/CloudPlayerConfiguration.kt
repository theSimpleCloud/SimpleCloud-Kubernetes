package app.simplecloud.simplecloud.api.player.configuration

import app.simplecloud.simplecloud.api.permission.configuration.PermissionPlayerConfiguration
import app.simplecloud.simplecloud.api.player.PlayerWebConfig
import java.util.*

/**
 * Date: 07.01.22
 * Time: 00:28
 * @author Frederick Baier
 *
 */
class CloudPlayerConfiguration(
    name: String,
    uniqueId: UUID,
    firstLogin: Long,
    lastLogin: Long,
    onlineTime: Long,
    playerConnection: PlayerConnectionConfiguration,
    displayName: String,
    webConfig: PlayerWebConfig,
    permissionPlayerConfiguration: PermissionPlayerConfiguration,
    val connectedServerName: String?,
    val connectedProxyName: String
) : OfflineCloudPlayerConfiguration(
    name,
    uniqueId,
    firstLogin,
    lastLogin,
    onlineTime,
    displayName,
    playerConnection,
    webConfig,
    permissionPlayerConfiguration
)