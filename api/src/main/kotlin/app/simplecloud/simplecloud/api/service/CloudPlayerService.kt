package app.simplecloud.simplecloud.api.service

import app.simplecloud.simplecloud.api.player.CloudPlayer
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Date: 07.01.22
 * Time: 18:08
 * @author Frederick Baier
 *
 */
interface CloudPlayerService : Service {

    fun findPlayerByUniqueId(uniqueId: UUID): CompletableFuture<CloudPlayer>

    fun findPlayerByName(name: String): CompletableFuture<CloudPlayer>

}