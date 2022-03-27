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

package app.simplecloud.simplecloud.api.impl.repository.ignite

import app.simplecloud.simplecloud.api.future.CloudCompletableFuture
import app.simplecloud.simplecloud.api.future.nonNull
import app.simplecloud.simplecloud.api.repository.Repository
import org.apache.ignite.IgniteCache
import org.apache.ignite.cache.query.ScanQuery
import org.apache.ignite.lang.IgniteBiPredicate
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 21.04.2021
 * Time: 19:09
 * @author Frederick Baier
 */
abstract class AbstractIgniteRepository<I: Any, T : Any>(
    private val igniteCache: IgniteCache<I, T>
) : Repository<I, T> {

    override fun findAll(): CompletableFuture<List<T>> {
        return CloudCompletableFuture.supplyAsync { this.igniteCache.toList().map { it.value } }.nonNull()
    }

    override fun findFirst(): CompletableFuture<T> {
        return CloudCompletableFuture.supplyAsync { this.igniteCache.first().value }.nonNull()
    }

    override fun find(identifier: I): CompletableFuture<T> {
        return findOrNull(identifier).nonNull(NoSuchElementException("No element found by id ${identifier}"))
    }

    override fun findOrNull(identifier: I): CompletableFuture<T?> {
        return CloudCompletableFuture.supplyAsync { this.igniteCache.get(identifier) }
    }

    override fun save(identifier: I, value: T): CompletableFuture<Unit> {
        return CloudCompletableFuture.runAsync {
            this.igniteCache.put(identifier, value)
        }
    }

    protected fun executeQuery(predicate: IgniteBiPredicate<*, T>): CompletableFuture<List<T>> {
        return CloudCompletableFuture.supplyAsync {
            val cursor = this.igniteCache.query(ScanQuery(predicate))
            return@supplyAsync cursor.all.map { it.value }
        }.nonNull()
    }

    protected fun executeQueryAndFindFirst(predicate: IgniteBiPredicate<*, T>): CompletableFuture<T> {
        return executeQuery(predicate).thenApply { it.first() }
    }

    override fun remove(identifier: I) {
        this.igniteCache.removeAsync(identifier)
    }

    override fun count(): CompletableFuture<Long> {
        return CloudCompletableFuture.supplyAsync {
            return@supplyAsync this.igniteCache.sizeLong()
        }.nonNull()
    }

}