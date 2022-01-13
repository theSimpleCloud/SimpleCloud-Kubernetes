package app.simplecloud.simplecloud.plugin.startup.service

import app.simplecloud.simplecloud.api.impl.player.CloudPlayerFactory
import app.simplecloud.simplecloud.api.impl.repository.ignite.IgniteCloudPlayerRepository
import app.simplecloud.simplecloud.api.impl.service.AbstractCloudPlayerService
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import com.google.inject.Inject
import java.util.concurrent.CompletableFuture

/**
 * Date: 13.01.22
 * Time: 18:40
 * @author Frederick Baier
 *
 */
class CloudPlayerServiceImpl @Inject constructor(
    private val igniteRepository: IgniteCloudPlayerRepository,
    playerFactory: CloudPlayerFactory
) : AbstractCloudPlayerService(igniteRepository, playerFactory) {

    override fun updateOfflinePlayerInternal(configuration: OfflineCloudPlayerConfiguration): CompletableFuture<Unit> {
        TODO()
    }
}