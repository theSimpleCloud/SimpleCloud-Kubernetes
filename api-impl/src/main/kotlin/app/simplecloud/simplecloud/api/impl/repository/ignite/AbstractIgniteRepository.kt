/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
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