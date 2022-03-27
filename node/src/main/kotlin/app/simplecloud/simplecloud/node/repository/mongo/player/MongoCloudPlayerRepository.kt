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

package app.simplecloud.simplecloud.node.repository.mongo.player

import app.simplecloud.simplecloud.api.future.CloudCompletableFuture
import app.simplecloud.simplecloud.api.future.nonNull
import app.simplecloud.simplecloud.node.repository.mongo.DefaultMongoRepository
import com.google.inject.Inject
import com.google.inject.Singleton
import dev.morphia.Datastore
import java.util.*
import java.util.concurrent.CompletableFuture

@Singleton
class MongoCloudPlayerRepository @Inject constructor(
    datastore: Datastore
) : DefaultMongoRepository<UUID, CloudPlayerEntity>(datastore, CloudPlayerEntity::class.java) {

    fun findByName(name: String): CompletableFuture<CloudPlayerEntity> {
        return CloudCompletableFuture.supplyAsync {
            createQuery("name", name).first()
        }.nonNull()
    }

}