package app.simplecloud.simplecloud.api.repository

import app.simplecloud.simplecloud.api.player.configuration.CloudPlayerConfiguration
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Date: 07.01.22
 * Time: 18:07
 * @author Frederick Baier
 *
 */
interface CloudPlayerRepository : Repository<UUID, CloudPlayerConfiguration> {

    fun findByName(name: String): CompletableFuture<CloudPlayerConfiguration>

}