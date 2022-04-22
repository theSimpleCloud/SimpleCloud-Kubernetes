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

package app.simplecloud.simplecloud.api.impl.repository.distributed

import app.simplecloud.simplecloud.api.future.CloudCompletableFuture
import app.simplecloud.simplecloud.api.future.nonNull
import app.simplecloud.simplecloud.api.repository.Repository
import app.simplecloud.simplecloud.distribution.api.Cache
import app.simplecloud.simplecloud.distribution.api.EntryListener
import app.simplecloud.simplecloud.distribution.api.Predicate
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 21.04.2021
 * Time: 19:09
 * @author Frederick Baier
 */
abstract class AbstractDistributedRepository<I : Any, T : Any>(
    private val cache: Cache<I, T>
) : Repository<I, T> {

    private val cacheName = this.cache.getName()

    override fun findAll(): CompletableFuture<List<T>> {
        return CloudCompletableFuture.supplyAsync { this.cache.toList().map { it.second } }
    }

    override fun findFirst(): CompletableFuture<T> {
        return CloudCompletableFuture.supplyAsync { this.cache.first().value }
    }

    override fun find(identifier: I): CompletableFuture<T> {
        return findOrNull(identifier).nonNull(NoSuchElementException("No element found by id ${identifier}"))
    }

    override fun findOrNull(identifier: I): CompletableFuture<T?> {
        return CloudCompletableFuture.supplyAsync { this.cache.get(identifier) }
    }

    override fun save(identifier: I, value: T): CompletableFuture<Unit> {
        return CloudCompletableFuture.runAsync {
            this.cache.put(identifier, value)
        }
    }

    protected fun executeQuery(predicate: Predicate<I, T>): CompletableFuture<Collection<T>> {
        return CloudCompletableFuture.supplyAsync {
            return@supplyAsync cache.distributedQuery(predicate)
        }
    }

    protected fun executeQueryAndFindFirst(predicate: Predicate<I, T>): CompletableFuture<T> {
        return executeQuery(predicate).thenApply { it.first() }
    }

    override fun remove(identifier: I) {
        CloudCompletableFuture.supplyAsync {
            this.cache.remove(identifier)
        }

    }

    override fun count(): CompletableFuture<Long> {
        return CloudCompletableFuture.supplyAsync {
            return@supplyAsync this.cache.size.toLong()
        }
    }

    fun addEntryListener(listener: EntryListener<I, T>) {
        this.cache.addEntryListener(listener)
    }

}