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
import app.simplecloud.simplecloud.api.impl.player.factory.CloudPlayerFactory
import app.simplecloud.simplecloud.api.impl.player.factory.OfflineCloudPlayerFactory
import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedCloudPlayerRepository
import app.simplecloud.simplecloud.api.impl.service.AbstractCloudPlayerService
import app.simplecloud.simplecloud.api.internal.configutation.PlayerLoginConfiguration
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.player.OfflineCloudPlayer
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.database.api.DatabaseOfflineCloudPlayerRepository
import app.simplecloud.simplecloud.node.player.CloudPlayerLoginHandler
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Date: 13.01.22
 * Time: 18:40
 * @author Frederick Baier
 *
 */
class CloudPlayerServiceImpl(
    distributedRepository: DistributedCloudPlayerRepository,
    private val playerFactory: CloudPlayerFactory,
    private val databaseCloudPlayerRepository: DatabaseOfflineCloudPlayerRepository,
    private val offlineCloudPlayerFactory: OfflineCloudPlayerFactory,
    private val cloudProcessService: CloudProcessService,
    private val cloudProcessGroupService: CloudProcessGroupService,
) : AbstractCloudPlayerService(distributedRepository, playerFactory) {

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
        val playerEntityFuture = this.databaseCloudPlayerRepository.findByName(name)
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
        val playerEntityFuture = this.databaseCloudPlayerRepository.find(uniqueId)
        return playerEntityFuture.thenApply { convertPlayerEntityToOfflineCloudPlayer(it) }
    }

    override suspend fun updateOfflinePlayerInternal(configuration: OfflineCloudPlayerConfiguration) {
        this.databaseCloudPlayerRepository.save(configuration.uniqueId, configuration).await()
    }

    override suspend fun loginPlayer(configuration: PlayerLoginConfiguration): CloudPlayer {
        val playerConfiguration = CloudPlayerLoginHandler(
            configuration,
            this.playerFactory,
            this.databaseCloudPlayerRepository,
            this,
            this.cloudProcessService,
            this.cloudProcessGroupService
        ).handleLogin()
        return this.playerFactory.create(playerConfiguration, this)
    }

    private fun convertPlayerEntityToOfflineCloudPlayer(configuration: OfflineCloudPlayerConfiguration): OfflineCloudPlayer {
        return this.offlineCloudPlayerFactory.create(configuration, this)
    }

}