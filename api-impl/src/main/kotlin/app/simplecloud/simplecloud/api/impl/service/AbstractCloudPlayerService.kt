package app.simplecloud.simplecloud.api.impl.service

import app.simplecloud.simplecloud.api.impl.player.CloudPlayerFactory
import app.simplecloud.simplecloud.api.impl.repository.ignite.IgniteCloudPlayerRepository
import app.simplecloud.simplecloud.api.internal.service.InternalCloudPlayerService
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.player.configuration.CloudPlayerConfiguration
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Date: 07.01.22
 * Time: 18:20
 * @author Frederick Baier
 *
 */
abstract class AbstractCloudPlayerService(
    private val igniteRepository: IgniteCloudPlayerRepository,
    private val playerFactory: CloudPlayerFactory
) : InternalCloudPlayerService {

    override fun findOnlinePlayerByUniqueId(uniqueId: UUID): CompletableFuture<CloudPlayer> {
        return this.igniteRepository.find(uniqueId).thenApply { this.playerFactory.create(it) }
    }

    override fun findOnlinePlayerByName(name: String): CompletableFuture<CloudPlayer> {
        return this.igniteRepository.findByName(name).thenApply { this.playerFactory.create(it) }
    }

    override fun updateOnlinePlayerInternal(configuration: CloudPlayerConfiguration): CompletableFuture<Unit> {
        return this.igniteRepository.save(configuration.uniqueId, configuration)
    }

}