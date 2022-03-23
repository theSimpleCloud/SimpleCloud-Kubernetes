package app.simplecloud.simplecloud.api.internal.service

import app.simplecloud.simplecloud.api.player.configuration.CloudPlayerConfiguration
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import app.simplecloud.simplecloud.api.service.CloudPlayerService

/**
 * Date: 11.01.22
 * Time: 19:33
 * @author Frederick Baier
 *
 */
interface InternalCloudPlayerService : CloudPlayerService {

    suspend fun updateOfflinePlayerInternal(configuration: OfflineCloudPlayerConfiguration)

    suspend fun updateOnlinePlayerInternal(configuration: CloudPlayerConfiguration)

}