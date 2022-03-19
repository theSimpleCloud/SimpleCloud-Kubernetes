package app.simplecloud.simplecloud.node.mongo

import app.simplecloud.simplecloud.api.future.completedFuture
import app.simplecloud.simplecloud.api.impl.repository.mongo.DefaultMongoRepository
import com.ea.async.Async.await
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

    fun loadObject(): CompletableFuture<T> {
        if (await(doesKeyExistInDatabase())) {
            return loadKeyFromDatabase()
        }
        return createNewClusterKeyAndSafeToDatabase()
    }

    private fun loadKeyFromDatabase(): CompletableFuture<T> {
        return this.keyRepository.find(this.idFieldName)
    }

    private fun createNewClusterKeyAndSafeToDatabase(): CompletableFuture<T> {
        val entity = this.entityClass.getDeclaredConstructor().newInstance()
        await(this.keyRepository.save(this.idFieldName, entity))
        return completedFuture(entity)
    }

    private fun doesKeyExistInDatabase(): CompletableFuture<Boolean> {
        return this.keyRepository.doesExist(this.idFieldName)
    }

}