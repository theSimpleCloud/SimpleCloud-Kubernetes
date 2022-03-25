package app.simplecloud.simplecloud.node.repository.mongo.strategymap

import app.simplecloud.simplecloud.api.future.CloudCompletableFuture
import app.simplecloud.simplecloud.api.future.nonNull
import app.simplecloud.simplecloud.node.repository.mongo.DefaultMongoRepository
import com.google.inject.Inject
import com.google.inject.Singleton
import dev.morphia.Datastore
import java.util.concurrent.CompletableFuture

/**
 * Date: 25.03.22
 * Time: 09:27
 * @author Frederick Baier
 *
 */
@Singleton
class MongoOnlineCountStrategyMapRepository @Inject constructor(
    datastore: Datastore
) : DefaultMongoRepository<String, OnlineCountStrategyMapEntity>(datastore, OnlineCountStrategyMapEntity::class.java) {

    fun findByStrategyName(onlineStrategyName: String): CompletableFuture<List<OnlineCountStrategyMapEntity>> {
        return CloudCompletableFuture.supplyAsync {
            createQuery("onlineStrategyName", onlineStrategyName).toList()
        }.nonNull()
    }

}