package app.simplecloud.simplecloud.api.player.configuration

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
    val connectedServerName: String?,
    val connectedProxyName: String
) : OfflineCloudPlayerConfiguration(
    name,
    uniqueId,
    firstLogin,
    lastLogin,
    onlineTime,
    displayName,
    playerConnection
)