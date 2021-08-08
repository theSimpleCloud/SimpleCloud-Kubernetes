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

package eu.thesimplecloud.simplecloud.api.impl.repository.mongo

import dev.morphia.Datastore
import dev.morphia.query.Query
import eu.thesimplecloud.simplecloud.api.repository.IRepository
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 08/08/2021
 * Time: 09:58
 * @author Frederick Baier
 */
open class DefaultMongoRepository<I : Any, T : Any>(
    private val datastore: Datastore,
    private val entityClass: Class<T>
) : IRepository<I, T> {

    override fun findAll(): CompletableFuture<List<T>> {
        return CompletableFuture.supplyAsync {
            this.datastore.find(entityClass).toList()
        }
    }

    override fun find(identifier: I): CompletableFuture<T> {
        return CompletableFuture.supplyAsync {
            createIdentifierQuery(identifier)
                .first() ?: throw NoSuchElementException("Element not found")
        }
    }

    override fun save(identifier: I, value: T) {
        CompletableFuture.supplyAsync {
            this.datastore.save(value)
        }
    }

    override fun remove(identifier: I) {
        CompletableFuture.supplyAsync {
            this.datastore.delete(createIdentifierQuery(identifier))
        }
    }

    override fun count(): CompletableFuture<Long> {
        return CompletableFuture.supplyAsync {
            this.datastore.find(entityClass).count()
        }
    }

    private fun createIdentifierQuery(identifier: I): Query<T> {
        return this.datastore.createQuery(entityClass)
            .field("_id")
            .equal(identifier)
    }
}