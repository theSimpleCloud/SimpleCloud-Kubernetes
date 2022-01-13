package app.simplecloud.simplecloud.api.impl.repository.ignite

import app.simplecloud.simplecloud.api.impl.ignite.predicate.CloudPlayerCompareNamePredicate
import app.simplecloud.simplecloud.api.player.configuration.CloudPlayerConfiguration
import app.simplecloud.simplecloud.api.repository.CloudPlayerRepository
import com.google.inject.Inject
import org.apache.ignite.Ignite
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Date: 07.01.22
 * Time: 18:10
 * @author Frederick Baier
 *
 */
class IgniteCloudPlayerRepository @Inject constructor(
    private val ignite: Ignite
) : AbstractIgniteRepository<UUID, CloudPlayerConfiguration>(
    ignite.getOrCreateCache("cloud-players")
), CloudPlayerRepository {

    override fun findByName(name: String): CompletableFuture<CloudPlayerConfiguration> {
        return executeQueryAndFindFirst(CloudPlayerCompareNamePredicate(name))
    }


}