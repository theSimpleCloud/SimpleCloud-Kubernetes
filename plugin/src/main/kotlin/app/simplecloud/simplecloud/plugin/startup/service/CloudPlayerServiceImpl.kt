package app.simplecloud.simplecloud.plugin.startup.service

import app.simplecloud.simplecloud.api.impl.player.CloudPlayerFactory
import app.simplecloud.simplecloud.api.impl.repository.ignite.IgniteCloudPlayerRepository
import app.simplecloud.simplecloud.api.impl.service.AbstractCloudPlayerService
import app.simplecloud.simplecloud.api.player.OfflineCloudPlayer
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import com.google.inject.Inject
import com.google.inject.Singleton
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Date: 13.01.22
 * Time: 18:40
 * @author Frederick Baier
 *
 */
@Singleton
class CloudPlayerServiceImpl @Inject constructor(
    private val igniteRepository: IgniteCloudPlayerRepository,
    playerFactory: CloudPlayerFactory
) : AbstractCloudPlayerService(igniteRepository, playerFactory) {

    override fun findOfflinePlayerByName(name: String): CompletableFuture<OfflineCloudPlayer> {
        TODO("Not yet implemented")
    }

    override fun findOfflinePlayerByUniqueId(uniqueId: UUID): CompletableFuture<OfflineCloudPlayer> {
        TODO("Not yet implemented")
    }

    override suspend fun updateOfflinePlayerInternal(configuration: OfflineCloudPlayerConfiguration) {
        TODO()
    }
}