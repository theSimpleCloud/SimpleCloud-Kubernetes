package app.simplecloud.simplecloud.node.service

import app.simplecloud.simplecloud.api.future.CloudCompletableFuture
import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.future.flatten
import app.simplecloud.simplecloud.api.future.isCompletedNormally
import app.simplecloud.simplecloud.api.impl.player.CloudPlayerFactory
import app.simplecloud.simplecloud.api.impl.player.OfflineCloudPlayerFactory
import app.simplecloud.simplecloud.api.impl.repository.ignite.IgniteCloudPlayerRepository
import app.simplecloud.simplecloud.api.impl.service.AbstractCloudPlayerService
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.player.OfflineCloudPlayer
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import app.simplecloud.simplecloud.node.mongo.player.CloudPlayerEntity
import app.simplecloud.simplecloud.node.mongo.player.MongoCloudPlayerRepository
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
    igniteRepository: IgniteCloudPlayerRepository,
    playerFactory: CloudPlayerFactory,
    private val mongoCloudPlayerRepository: MongoCloudPlayerRepository,
    private val offlineCloudPlayerFactory: OfflineCloudPlayerFactory
) : AbstractCloudPlayerService(igniteRepository, playerFactory) {

    override fun findOfflinePlayerByName(name: String): CompletableFuture<OfflineCloudPlayer> {
        val onlinePlayerFuture = findOnlinePlayerByName(name)
        return onlinePlayerFuture.handle { _, _ -> findOnlinePlayerByName0(name, onlinePlayerFuture) }.flatten()
    }

    private fun findOnlinePlayerByName0(
        name: String,
        completedOnlinePlayerFuture: CompletableFuture<CloudPlayer>
    ): CompletableFuture<OfflineCloudPlayer> {
        if (completedOnlinePlayerFuture.isCompletedNormally) {
            return CloudCompletableFuture.completedFuture(completedOnlinePlayerFuture.get())
        }
        val playerEntityFuture = this.mongoCloudPlayerRepository.findByName(name)
        return playerEntityFuture.thenApply { convertPlayerEntityToOfflineCloudPlayer(it) }
    }

    override fun findOfflinePlayerByUniqueId(uniqueId: UUID): CompletableFuture<OfflineCloudPlayer> {
        val onlinePlayerFuture = findOnlinePlayerByUniqueId(uniqueId)
        return onlinePlayerFuture.handle { _, _ -> findOfflinePlayerByUniqueId0(uniqueId, onlinePlayerFuture) }
            .flatten()
    }

    private fun findOfflinePlayerByUniqueId0(
        uniqueId: UUID,
        completedOnlinePlayerFuture: CompletableFuture<CloudPlayer>
    ): CompletableFuture<OfflineCloudPlayer> {
        if (completedOnlinePlayerFuture.isCompletedNormally) {
            return CloudCompletableFuture.completedFuture(completedOnlinePlayerFuture.get())
        }
        val playerEntityFuture = this.mongoCloudPlayerRepository.find(uniqueId)
        return playerEntityFuture.thenApply { convertPlayerEntityToOfflineCloudPlayer(it) }
    }

    override suspend fun updateOfflinePlayerInternal(configuration: OfflineCloudPlayerConfiguration){
        val playerEntity = CloudPlayerEntity.fromConfiguration(configuration)
        this.mongoCloudPlayerRepository.save(configuration.uniqueId, playerEntity).await()
    }

    private fun convertPlayerEntityToOfflineCloudPlayer(entity: CloudPlayerEntity): OfflineCloudPlayer {
        return this.offlineCloudPlayerFactory.create(entity.toConfiguration())
    }

}