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

package app.simplecloud.simplecloud.database.mongo

import app.simplecloud.simplecloud.api.future.CloudCompletableFuture
import app.simplecloud.simplecloud.api.future.nonNull
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import app.simplecloud.simplecloud.database.api.DatabaseOfflineCloudPlayerRepository
import app.simplecloud.simplecloud.database.mongo.entity.CloudPlayerEntity
import dev.morphia.Datastore
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Date: 23.04.22
 * Time: 19:03
 * @author Frederick Baier
 *
 */
class MongoDatabaseOfflineCloudPlayerRepositoryAdapter(
    datastore: Datastore
) : DatabaseOfflineCloudPlayerRepository {

    private val mongoRepository =
        DefaultMongoRepository<UUID, CloudPlayerEntity>(datastore, CloudPlayerEntity::class.java)

    override fun findByName(name: String): CompletableFuture<OfflineCloudPlayerConfiguration> {
        return CloudCompletableFuture.supplyAsync {
            val entity = this.mongoRepository.createQuery("name", name).first()
            return@supplyAsync entity?.toConfiguration()
        }.nonNull(NoSuchElementException("Cannot find player by name '${name}'"))
    }

    override fun findAll(): CompletableFuture<List<OfflineCloudPlayerConfiguration>> {
        return this.mongoRepository.findAll().thenApply { list -> list.map { it.toConfiguration() } }
    }

    override fun findFirst(): CompletableFuture<OfflineCloudPlayerConfiguration> {
        return this.mongoRepository.findFirst().thenApply { it.toConfiguration() }
    }

    override fun find(identifier: UUID): CompletableFuture<OfflineCloudPlayerConfiguration> {
        return this.mongoRepository.find(identifier).thenApply { it.toConfiguration() }
    }

    override fun findOrNull(identifier: UUID): CompletableFuture<OfflineCloudPlayerConfiguration?> {
        return this.mongoRepository.findOrNull(identifier).thenApply { it?.toConfiguration() }
    }

    override fun save(identifier: UUID, value: OfflineCloudPlayerConfiguration): CompletableFuture<Unit> {
        return this.mongoRepository.save(identifier, CloudPlayerEntity.fromConfiguration(value))
    }

    override fun remove(identifier: UUID): CompletableFuture<Unit> {
        return this.mongoRepository.remove(identifier)
    }

    override fun count(): CompletableFuture<Long> {
        return this.mongoRepository.count()
    }


}