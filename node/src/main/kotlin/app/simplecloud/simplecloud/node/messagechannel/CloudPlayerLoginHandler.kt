package app.simplecloud.simplecloud.node.messagechannel

import app.simplecloud.simplecloud.api.future.completedFuture
import app.simplecloud.simplecloud.api.future.exception.CompletedWithNullException
import app.simplecloud.simplecloud.api.future.unpackFutureException
import app.simplecloud.simplecloud.api.impl.player.CloudPlayerFactory
import app.simplecloud.simplecloud.api.permission.configuration.PermissionPlayerConfiguration
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.player.PlayerWebConfig
import app.simplecloud.simplecloud.api.player.configuration.CloudPlayerConfiguration
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.node.mongo.player.CloudPlayerEntity
import app.simplecloud.simplecloud.node.mongo.player.MongoCloudPlayerRepository
import com.ea.async.Async.await
import org.apache.logging.log4j.LogManager
import java.util.concurrent.CompletableFuture

class CloudPlayerLoginHandler(
    private val playerFactory: CloudPlayerFactory,
    private val mongoPlayerRepository: MongoCloudPlayerRepository,
    private val configuration: PlayerConnectionConfiguration,
    private val proxyName: String
) {

    fun handleLogin(): CompletableFuture<CloudPlayerConfiguration> {
        logger.info("Player {} is logging in on {}", this.configuration.name, this.proxyName)
        val player = await(createPlayer())
        savePlayerToDatabase(player)
        player.createUpdateRequest().submit()
        return completedFuture(player.toConfiguration())
    }

    private fun savePlayerToDatabase(player: CloudPlayer) {
        val configuration = player.toConfiguration()
        val playerEntity = CloudPlayerEntity.fromConfiguration(configuration)
        this.mongoPlayerRepository.save(playerEntity.uniqueId, playerEntity)
    }

    private fun createPlayer(): CompletableFuture<CloudPlayer> {
        try {
            val loadedPlayerConfiguration = await(loadPlayerFromDatabase())
            return completedFuture(createPlayerFromConfiguration(loadedPlayerConfiguration))
        } catch (e: Exception) {
            val unpackedException = unpackFutureException(e)
            if (unpackedException is CompletedWithNullException) {
                return completedFuture(createNewCloudPlayer())
            }
            throw e
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
            "",
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

    private fun loadPlayerFromDatabase(): CompletableFuture<OfflineCloudPlayerConfiguration> {
        return this.mongoPlayerRepository.find(this.configuration.uniqueId).thenApply { it.toConfiguration() }
    }

    companion object {
        private val logger = LogManager.getLogger(CloudPlayerLoginHandler::class.java)
    }

}
