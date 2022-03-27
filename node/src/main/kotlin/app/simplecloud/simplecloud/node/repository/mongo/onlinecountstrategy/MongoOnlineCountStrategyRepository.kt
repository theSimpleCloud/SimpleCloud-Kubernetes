package app.simplecloud.simplecloud.node.repository.mongo.onlinecountstrategy

import app.simplecloud.simplecloud.node.repository.mongo.DefaultMongoRepository
import com.google.inject.Inject
import com.google.inject.Singleton
import dev.morphia.Datastore

/**
 * Date: 25.03.22
 * Time: 09:27
 * @author Frederick Baier
 *
 */
@Singleton
class MongoOnlineCountStrategyRepository @Inject constructor(
    datastore: Datastore
) : DefaultMongoRepository<String, OnlineCountStrategyEntity>(datastore, OnlineCountStrategyEntity::class.java)