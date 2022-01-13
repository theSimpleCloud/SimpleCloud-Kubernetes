package app.simplecloud.simplecloud.api.internal.service

import app.simplecloud.simplecloud.api.player.configuration.CloudPlayerConfiguration
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import app.simplecloud.simplecloud.api.service.CloudPlayerService
import java.util.concurrent.CompletableFuture

/**
 * Date: 11.01.22
 * Time: 19:33
 * @author Frederick Baier
 *
 */
interface InternalCloudPlayerService : CloudPlayerService {

    fun updateOfflinePlayerInternal(configuration: OfflineCloudPlayerConfiguration): CompletableFuture<Unit>

    fun updateOnlinePlayerInternal(configuration: CloudPlayerConfiguration): CompletableFuture<Unit>

}