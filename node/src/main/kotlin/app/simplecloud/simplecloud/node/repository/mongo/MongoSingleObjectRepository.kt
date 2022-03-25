package app.simplecloud.simplecloud.node.repository.mongo

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.future.future
import dev.morphia.Datastore
import java.util.concurrent.CompletableFuture

/**
 * Date: 19.03.22
 * Time: 10:05
 * @author Frederick Baier
 *
 */
class MongoSingleObjectRepository<T : Any>(
    datastore: Datastore,
    //Class must have a no-args constructor with initial values, which will be saved in the database
    private val entityClass: Class<T>,
    private val idFieldName: String
) {

    private val keyRepository = DefaultMongoRepository<String, T>(datastore, entityClass)

    fun loadObject(): CompletableFuture<T> = CloudScope.future {
        if (doesKeyExistInDatabase()) {
            return@future loadKeyFromDatabase()
        }
        return@future createNewClusterKeyAndSafeToDatabase()
    }

    private suspend fun loadKeyFromDatabase(): T {
        return this.keyRepository.find(this.idFieldName).await()
    }

    private suspend fun createNewClusterKeyAndSafeToDatabase(): T {
        val entity = this.entityClass.getDeclaredConstructor().newInstance()
        this.keyRepository.save(this.idFieldName, entity).await()
        return entity
    }

    private suspend fun doesKeyExistInDatabase(): Boolean {
        return this.keyRepository.doesExist(this.idFieldName).await()
    }

}