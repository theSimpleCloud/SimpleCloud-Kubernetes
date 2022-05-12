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

package app.simplecloud.simplecloud.database.memory

import app.simplecloud.simplecloud.api.future.CloudCompletableFuture
import app.simplecloud.simplecloud.api.future.nonNull
import app.simplecloud.simplecloud.api.future.unitFuture
import app.simplecloud.simplecloud.api.repository.Repository
import app.simplecloud.simplecloud.distribution.api.Predicate
import com.google.common.collect.Maps
import java.util.concurrent.CompletableFuture

open class InMemoryRepository<I : Any, T : Any> : Repository<I, T> {

    private val map = Maps.newConcurrentMap<I, T>()

    override fun findAll(): CompletableFuture<List<T>> {
        return CloudCompletableFuture.supplyAsync {
            return@supplyAsync this.map.values.toList()
        }
    }

    override fun findFirst(): CompletableFuture<T> {
        return CloudCompletableFuture.supplyAsync {
            return@supplyAsync this.map.values.first()
        }
    }

    override fun find(identifier: I): CompletableFuture<T> {
        return findOrNull(identifier).nonNull(NoSuchElementException())
    }

    override fun findOrNull(identifier: I): CompletableFuture<T?> {
        return CloudCompletableFuture.supplyAsync {
            return@supplyAsync this.map[identifier]
        }
    }

    override fun save(identifier: I, value: T): CompletableFuture<Unit> {
        this.map[identifier] = value
        return unitFuture()
    }

    override fun remove(identifier: I): CompletableFuture<Unit> {
        this.map.remove(identifier)
        return unitFuture()
    }

    override fun count(): CompletableFuture<Long> {
        return CompletableFuture.supplyAsync {
            return@supplyAsync this.map.size.toLong()
        }
    }

    fun executeQuery(predicate: Predicate<I, T>): CompletableFuture<Collection<T>> {
        return CloudCompletableFuture.supplyAsync {
            return@supplyAsync this.map.filter { predicate.apply(it.key, it.value) }.map { it.value }
        }
    }

    fun executeQueryAndFindFist(predicate: Predicate<I, T>): CompletableFuture<T> {
        return executeQuery(predicate).thenApply { it.first() }
    }

}
