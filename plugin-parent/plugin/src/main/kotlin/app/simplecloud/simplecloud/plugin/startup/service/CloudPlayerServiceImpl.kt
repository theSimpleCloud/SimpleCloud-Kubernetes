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

package app.simplecloud.simplecloud.plugin.startup.service

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.impl.player.factory.CloudPlayerFactory
import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedCloudPlayerRepository
import app.simplecloud.simplecloud.api.impl.service.AbstractCloudPlayerService
import app.simplecloud.simplecloud.api.internal.configutation.PlayerLoginConfiguration
import app.simplecloud.simplecloud.api.internal.messagechannel.InternalMessageChannelProvider
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.player.OfflineCloudPlayer
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import app.simplecloud.simplecloud.api.service.NodeService
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
    private val nodeService: NodeService,
    internalMessageChannelProvider: InternalMessageChannelProvider,
    private val playerFactory: CloudPlayerFactory
) : AbstractCloudPlayerService(distributedRepository, playerFactory) {

    private val loginMessageChannel = internalMessageChannelProvider.getInternalPlayerLoginChannel()

    override fun findOfflinePlayerByName(name: String): CompletableFuture<OfflineCloudPlayer> {
        TODO("Not yet implemented")
    }

    override fun findOfflinePlayerByUniqueId(uniqueId: UUID): CompletableFuture<OfflineCloudPlayer> {
        TODO("Not yet implemented")
    }

    override suspend fun updateOfflinePlayerInternal(configuration: OfflineCloudPlayerConfiguration) {
        TODO()
    }

    override suspend fun logoutPlayer(uniqueId: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun loginPlayer(configuration: PlayerLoginConfiguration): CloudPlayer {
        val node = this.nodeService.findFirst().await()
        val playerConfiguration = this.loginMessageChannel.createMessageRequest(configuration, node).submit().await()
        return this.playerFactory.create(playerConfiguration, this)
    }
}