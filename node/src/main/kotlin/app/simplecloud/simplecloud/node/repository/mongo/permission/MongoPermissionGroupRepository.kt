package app.simplecloud.simplecloud.node.repository.mongo.permission

import app.simplecloud.simplecloud.node.repository.mongo.DefaultMongoRepository
import com.google.inject.Inject
import com.google.inject.Singleton
import dev.morphia.Datastore

/**
 * Date: 20.03.22
 * Time: 13:11
 * @author Frederick Baier
 *
 */
@Singleton
class MongoPermissionGroupRepository @Inject constructor(
    datastore: Datastore
) : DefaultMongoRepository<String, PermissionGroupEntity>(datastore, PermissionGroupEntity::class.java)