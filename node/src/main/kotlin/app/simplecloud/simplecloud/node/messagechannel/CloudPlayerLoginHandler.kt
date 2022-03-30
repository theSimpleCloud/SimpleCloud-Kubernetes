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

package app.simplecloud.simplecloud.node.messagechannel

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.impl.player.CloudPlayerFactory
import app.simplecloud.simplecloud.api.permission.configuration.PermissionPlayerConfiguration
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.player.PlayerWebConfig
import app.simplecloud.simplecloud.api.player.configuration.CloudPlayerConfiguration
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.node.repository.mongo.player.CloudPlayerEntity
import app.simplecloud.simplecloud.node.repository.mongo.player.MongoCloudPlayerRepository
import org.apache.logging.log4j.LogManager

class CloudPlayerLoginHandler(
    private val playerFactory: CloudPlayerFactory,
    private val mongoPlayerRepository: MongoCloudPlayerRepository,
    private val configuration: PlayerConnectionConfiguration,
    private val proxyName: String
) {

    suspend fun handleLogin(): CloudPlayerConfiguration {
        logger.info("Player {} is logging in on {}", this.configuration.name, this.proxyName)
        val player = createPlayer()
        savePlayerToDatabase(player)
        player.createUpdateRequest().submit()
        return player.toConfiguration()
    }

    private fun savePlayerToDatabase(player: CloudPlayer) {
        val configuration = player.toConfiguration()
        val playerEntity = CloudPlayerEntity.fromConfiguration(configuration)
        this.mongoPlayerRepository.save(playerEntity.uniqueId, playerEntity)
    }

    private suspend fun createPlayer(): CloudPlayer {
        try {
            val loadedPlayerConfiguration = loadPlayerFromDatabase()
            return createPlayerFromConfiguration(loadedPlayerConfiguration)
        } catch (e: NoSuchElementException) {
            return createNewCloudPlayer()
        }
    }

    private fun createNewCloudPlayer(): CloudPlayer {
        val cloudPlayerConfiguration = CloudPlayerConfiguration(
            this.configuration.name,
            this.configuration.uniqueId,
            System.currentTimeMillis(),
            System.currentTimeMillis(),
            0,
            this.configuration,
            this.configuration.name,
            PlayerWebConfig("", false),
            PermissionPlayerConfiguration(
                this.configuration.uniqueId,
                emptyList()
            ),
            null,
            this.proxyName
        )
        return this.playerFactory.create(cloudPlayerConfiguration)
    }

    private fun createPlayerFromConfiguration(loadedPlayerConfiguration: OfflineCloudPlayerConfiguration): CloudPlayer {
        val cloudPlayerConfiguration = createCloudPlayerConfiguration(loadedPlayerConfiguration)
        return this.playerFactory.create(cloudPlayerConfiguration)
    }

    private fun createCloudPlayerConfiguration(loadedPlayerConfiguration: OfflineCloudPlayerConfiguration): CloudPlayerConfiguration {
        return CloudPlayerConfiguration(
            this.configuration.name,
            this.configuration.uniqueId,
            loadedPlayerConfiguration.firstLogin,
            System.currentTimeMillis(),
            loadedPlayerConfiguration.onlineTime,
            this.configuration,
            loadedPlayerConfiguration.displayName,
            loadedPlayerConfiguration.webConfig,
            loadedPlayerConfiguration.permissionPlayerConfiguration,
            null,
            proxyName
        )
    }

    private suspend fun loadPlayerFromDatabase(): OfflineCloudPlayerConfiguration {
        val cloudPlayerEntity = this.mongoPlayerRepository.find(this.configuration.uniqueId).await()
        return cloudPlayerEntity.toConfiguration()
    }

    companion object {
        private val logger = LogManager.getLogger(CloudPlayerLoginHandler::class.java)
    }

}
