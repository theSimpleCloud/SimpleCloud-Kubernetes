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

package app.simplecloud.simplecloud.node.player

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.impl.player.factory.CloudPlayerFactory
import app.simplecloud.simplecloud.api.internal.configutation.PlayerLoginConfiguration
import app.simplecloud.simplecloud.api.internal.exception.PlayerAlreadyRegisteredException
import app.simplecloud.simplecloud.api.internal.exception.UnknownProxyProcessException
import app.simplecloud.simplecloud.api.internal.service.InternalCloudPlayerService
import app.simplecloud.simplecloud.api.permission.configuration.PermissionPlayerConfiguration
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.player.PlayerWebConfig
import app.simplecloud.simplecloud.api.player.configuration.CloudPlayerConfiguration
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.ResourceRequestHandler
import app.simplecloud.simplecloud.node.resource.player.V1Beta1CloudPlayerSpec
import org.apache.logging.log4j.LogManager

class CloudPlayerLoginHandler(
    private val configuration: PlayerLoginConfiguration,
    private val playerFactory: CloudPlayerFactory,
    private val playerService: InternalCloudPlayerService,
    private val processService: CloudProcessService,
    private val groupService: CloudProcessGroupService,
    private val requestHandler: ResourceRequestHandler,
) {

    suspend fun handleLogin(): CloudPlayerConfiguration {
        checkProxyConnectingToExists()
        logger.info(
            "Player {} is logging in on {}",
            this.configuration.connectionConfiguration.name,
            this.configuration.proxyName
        )
        checkPlayerAlreadyConnected()
        val player = createPlayer()
        checkJoinPermission(player)
        player.createUpdateRequest().submit().await()
        return player.toConfiguration()
    }

    private suspend fun checkJoinPermission(player: CloudPlayer) {
        val proxyProcess = this.processService.findByName(configuration.proxyName).await()
        CloudPlayerLoginJoinPermissionChecker(player, proxyProcess, this.groupService).check()
    }

    private suspend fun checkProxyConnectingToExists() {
        try {
            this.processService.findByName(configuration.proxyName).await()
        } catch (e: NoSuchElementException) {
            throw UnknownProxyProcessException(configuration.proxyName)
        }
    }

    private suspend fun checkPlayerAlreadyConnected() {
        if (doesPlayerAlreadyExist()) {
            throw PlayerAlreadyRegisteredException(this.configuration)
        }
    }


    private suspend fun doesPlayerAlreadyExist(): Boolean {
        return runCatching {
            this.playerService.findOnlinePlayerByUniqueId(this.configuration.connectionConfiguration.uniqueId).await()
        }.isSuccess
    }

    private suspend fun createPlayer(): CloudPlayer {
        try {
            val loadedPlayerConfiguration = loadPlayerFromDatabase()
            return createPlayerFromConfiguration(loadedPlayerConfiguration)
        } catch (e: NoSuchElementException) {
            val newPlayer = createNewCloudPlayer()
            this.requestHandler.handleCreate(
                "core",
                "CloudPlayer",
                "v1beta1",
                newPlayer.getUniqueId().toString(),
                V1Beta1CloudPlayerSpec.fromConfig(newPlayer.toConfiguration())
            )
            return newPlayer
        }
    }

    private fun createNewCloudPlayer(): CloudPlayer {
        val connectionConfiguration = this.configuration.connectionConfiguration
        val cloudPlayerConfiguration = CloudPlayerConfiguration(
            connectionConfiguration.name,
            connectionConfiguration.uniqueId,
            System.currentTimeMillis(),
            System.currentTimeMillis(),
            0,
            connectionConfiguration,
            connectionConfiguration.name,
            PlayerWebConfig("", false),
            PermissionPlayerConfiguration(
                connectionConfiguration.uniqueId,
                emptyList()
            ),
            null,
            this.configuration.proxyName
        )
        return this.playerFactory.create(cloudPlayerConfiguration, this.playerService)
    }

    private fun createPlayerFromConfiguration(loadedPlayerConfiguration: OfflineCloudPlayerConfiguration): CloudPlayer {
        val cloudPlayerConfiguration = createCloudPlayerConfiguration(loadedPlayerConfiguration)
        return this.playerFactory.create(cloudPlayerConfiguration, this.playerService)
    }

    private fun createCloudPlayerConfiguration(loadedPlayerConfiguration: OfflineCloudPlayerConfiguration): CloudPlayerConfiguration {
        val connectionConfiguration = this.configuration.connectionConfiguration
        return CloudPlayerConfiguration(
            connectionConfiguration.name,
            connectionConfiguration.uniqueId,
            loadedPlayerConfiguration.firstLogin,
            System.currentTimeMillis(),
            loadedPlayerConfiguration.onlineTime,
            connectionConfiguration,
            loadedPlayerConfiguration.displayName,
            loadedPlayerConfiguration.webConfig,
            loadedPlayerConfiguration.permissionPlayerConfiguration,
            null,
            this.configuration.proxyName
        )
    }

    private suspend fun loadPlayerFromDatabase(): OfflineCloudPlayerConfiguration {
        val playerUniqueId = this.configuration.connectionConfiguration.uniqueId
        return this.requestHandler.handleGetOneSpec<V1Beta1CloudPlayerSpec>(
            "core",
            "CloudPlayer",
            "v1beta1",
            playerUniqueId.toString()
        ).getSpec().toOfflineCloudPlayerConfig(playerUniqueId)
    }

    companion object {
        private val logger = LogManager.getLogger(CloudPlayerLoginHandler::class.java)
    }

}
