package app.simplecloud.simplecloud.api.player.configuration

import java.util.*

/**
 * Date: 07.01.22
 * Time: 00:25
 * @author Frederick Baier
 *
 */
open class OfflineCloudPlayerConfiguration(
    val name: String,
    val uniqueId: UUID,
    val firstLogin: Long,
    val lastLogin: Long,
    val onlineTime: Long,
    val displayName: String,
    val lastPlayerConnection: PlayerConnectionConfiguration
)