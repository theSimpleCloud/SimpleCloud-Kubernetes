/*
 * SimpleCloud is a software for administrating a minecraft server network.
 * Copyright (C) 2022 Frederick Baier & Philipp Eistrach
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
import app.simplecloud.simplecloud.node.repository.mongo.player.CloudPlayerEntity
import app.simplecloud.simplecloud.node.repository.mongo.player.MongoCloudPlayerRepository
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
        return onlinePlayerFuture.handle { _, _ -> findOfflinePlayerByName0(name, onlinePlayerFuture) }.flatten()
    }

    private fun findOfflinePlayerByName0(
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