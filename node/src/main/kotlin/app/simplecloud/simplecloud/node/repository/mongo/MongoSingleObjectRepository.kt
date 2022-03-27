/*
 * SimpleCloud is a software for administrating a minecraft server network.
 * Copyright (C) 2022 Frederick Baier & Philipp Eistrach
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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