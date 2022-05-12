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

import app.simplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import app.simplecloud.simplecloud.database.api.DatabaseCloudProcessGroupRepository
import app.simplecloud.simplecloud.database.mongo.entity.CombinedProcessGroupEntity
import dev.morphia.Datastore
import java.util.concurrent.CompletableFuture

/**
 * Date: 23.04.22
 * Time: 19:03
 * @author Frederick Baier
 *
 */
class MongoDatabaseCloudProcessGroupRepositoryAdapter(
    datastore: Datastore
) : DatabaseCloudProcessGroupRepository {

    private val mongoRepository =
        DefaultMongoRepository<String, CombinedProcessGroupEntity>(datastore, CombinedProcessGroupEntity::class.java)


    override fun findAll(): CompletableFuture<List<AbstractCloudProcessGroupConfiguration>> {
        return this.mongoRepository.findAll().thenApply { list -> list.map { it.toConfiguration() } }
    }

    override fun findFirst(): CompletableFuture<AbstractCloudProcessGroupConfiguration> {
        return this.mongoRepository.findFirst().thenApply { it.toConfiguration() }
    }

    override fun find(identifier: String): CompletableFuture<AbstractCloudProcessGroupConfiguration> {
        return this.mongoRepository.find(identifier).thenApply { it.toConfiguration() }
    }

    override fun findOrNull(identifier: String): CompletableFuture<AbstractCloudProcessGroupConfiguration?> {
        return this.mongoRepository.findOrNull(identifier).thenApply { it?.toConfiguration() }
    }

    override fun save(identifier: String, value: AbstractCloudProcessGroupConfiguration): CompletableFuture<Unit> {
        return this.mongoRepository.save(identifier, CombinedProcessGroupEntity.fromGroupConfiguration(value))
    }

    override fun remove(identifier: String): CompletableFuture<Unit> {
        return this.mongoRepository.remove(identifier)
    }

    override fun count(): CompletableFuture<Long> {
        return this.mongoRepository.count()
    }


}