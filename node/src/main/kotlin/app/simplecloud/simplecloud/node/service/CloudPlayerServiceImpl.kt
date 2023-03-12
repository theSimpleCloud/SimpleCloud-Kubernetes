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

import app.simplecloud.simplecloud.api.future.await
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
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.ResourceRequestHandler
import app.simplecloud.simplecloud.node.player.CloudPlayerLoginHandler
import app.simplecloud.simplecloud.node.player.CloudPlayerLogoutHandler
import app.simplecloud.simplecloud.node.resource.player.V1Beta1CloudPlayerSpec
import app.simplecloud.simplecloud.node.resource.player.V1Beta1CloudPlayerStatus
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
    private val offlineCloudPlayerFactory: OfflineCloudPlayerFactory,
    private val cloudProcessService: CloudProcessService,
    private val cloudProcessGroupService: CloudProcessGroupService,
    private val requestHandler: ResourceRequestHandler,
) : AbstractCloudPlayerService(distributedRepository, playerFactory) {

    override fun findOfflinePlayerByName(name: String): CompletableFuture<OfflineCloudPlayer> {
        val onlinePlayerFuture = findOnlinePlayerByName(name)
        return onlinePlayerFuture.handle { _, _ -> findOfflinePlayerByName0(name, onlinePlayerFuture) }
    }

    private fun findOfflinePlayerByName0(
        name: String,
        completedOnlinePlayerFuture: CompletableFuture<CloudPlayer>,
    ): OfflineCloudPlayer {
        if (completedOnlinePlayerFuture.isCompletedNormally) {
            return completedOnlinePlayerFuture.get()
        }
        val specAndStatus =
            this.requestHandler.handleGetOneSpecAndStatus<V1Beta1CloudPlayerSpec, V1Beta1CloudPlayerStatus>(
                "core",
                "CloudPlayer",
                "v1beta1",
                "spec.name",
                name
            )
        return convertPlayerEntityToOfflineCloudPlayer(
            specAndStatus.getSpec().toOfflineCloudPlayerConfig(UUID.fromString(specAndStatus.getName()))
        )
    }

    override fun findOfflinePlayerByUniqueId(uniqueId: UUID): CompletableFuture<OfflineCloudPlayer> {
        val onlinePlayerFuture = findOnlinePlayerByUniqueId(uniqueId)
        return onlinePlayerFuture.handle { _, _ -> findOfflinePlayerByUniqueId0(uniqueId, onlinePlayerFuture) }
    }

    private fun findOfflinePlayerByUniqueId0(
        uniqueId: UUID,
        completedOnlinePlayerFuture: CompletableFuture<CloudPlayer>,
    ): OfflineCloudPlayer {
        if (completedOnlinePlayerFuture.isCompletedNormally) {
            return completedOnlinePlayerFuture.get()
        }
        val specResult = this.requestHandler.handleGetOneSpec<V1Beta1CloudPlayerSpec>(
            "core",
            "CloudPlayer",
            "v1beta1",
            uniqueId.toString()
        )
        return convertPlayerEntityToOfflineCloudPlayer(specResult.getSpec().toOfflineCloudPlayerConfig(uniqueId))
    }

    override suspend fun updateOfflinePlayerInternal(configuration: OfflineCloudPlayerConfiguration) {
        this.requestHandler.handleUpdate(
            "core",
            "CloudPlayer",
            "v1beta1",
            configuration.uniqueId.toString(),
            V1Beta1CloudPlayerSpec.fromConfig(configuration)
        )
    }

    override suspend fun loginPlayer(configuration: PlayerLoginConfiguration): CloudPlayer {
        val playerConfiguration = CloudPlayerLoginHandler(
            configuration,
            this.playerFactory,
            this,
            this.cloudProcessService,
            this.cloudProcessGroupService,
            this.requestHandler
        ).handleLogin()
        return this.playerFactory.create(playerConfiguration, this)
    }

    override suspend fun logoutPlayer(uniqueId: UUID) {
        val onlinePlayer = findOnlinePlayerByUniqueId(uniqueId).await()
        CloudPlayerLogoutHandler(
            onlinePlayer,
            this.distributedRepository,
            this.requestHandler
        ).handleLogout()
    }


    private fun convertPlayerEntityToOfflineCloudPlayer(configuration: OfflineCloudPlayerConfiguration): OfflineCloudPlayer {
        return this.offlineCloudPlayerFactory.create(configuration, this)
    }

}