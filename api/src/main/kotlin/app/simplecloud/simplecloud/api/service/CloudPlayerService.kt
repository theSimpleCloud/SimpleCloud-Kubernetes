package app.simplecloud.simplecloud.api.service

import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.player.OfflineCloudPlayer
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Date: 07.01.22
 * Time: 18:08
 * @author Frederick Baier
 *
 */
interface CloudPlayerService : Service {

    fun findOnlinePlayerByUniqueId(uniqueId: UUID): CompletableFuture<CloudPlayer>

    fun findOnlinePlayerByName(name: String): CompletableFuture<CloudPlayer>

    fun findOfflinePlayerByName(name: String): CompletableFuture<OfflineCloudPlayer>

    fun findOfflinePlayerByUniqueId(uniqueId: UUID): CompletableFuture<OfflineCloudPlayer>

}