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
import app.simplecloud.simplecloud.api.repository.Repository
import dev.morphia.Datastore
import dev.morphia.query.Query
import dev.morphia.query.filters.Filters
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 08/08/2021
 * Time: 09:58
 * @author Frederick Baier
 */
class DefaultMongoRepository<I : Any, T : Any>(
    private val datastore: Datastore,
    private val entityClass: Class<T>
) : Repository<I, T> {

    override fun findAll(): CompletableFuture<List<T>> {
        return CloudCompletableFuture.supplyAsync {
            this.datastore.find(entityClass).toList()
        }
    }

    override fun find(identifier: I): CompletableFuture<T> {
        return findOrNull(identifier).nonNull(NoSuchElementException("No element found by id ${identifier}"))
    }

    override fun findOrNull(identifier: I): CompletableFuture<T?> {
        return CloudCompletableFuture.supplyAsync {
            createIdentifierQuery(identifier).first()
        }
    }

    override fun save(identifier: I, value: T): CompletableFuture<Unit> {
        return CloudCompletableFuture.runAsync {
            this.datastore.save(value)
        }
    }

    override fun remove(identifier: I): CompletableFuture<Unit> {
        return CloudCompletableFuture.supplyAsync {
            createIdentifierQuery(identifier).delete()
        }
    }

    override fun count(): CompletableFuture<Long> {
        return CloudCompletableFuture.supplyAsync {
            this.datastore.find(this.entityClass).count()
        }
    }

    private fun createIdentifierQuery(identifier: I): Query<T> {
        return this.datastore.find(this.entityClass)
            .filter(Filters.eq("_id", identifier))
    }

    override fun findFirst(): CompletableFuture<T> {
        return CloudCompletableFuture.supplyAsync {
            this.datastore.find(this.entityClass).first()
        }.nonNull()
    }

    fun createQuery(field: String, value: Any): Query<T> {
        return this.datastore.find(this.entityClass)
            .filter(Filters.eq(field, value))
    }
}