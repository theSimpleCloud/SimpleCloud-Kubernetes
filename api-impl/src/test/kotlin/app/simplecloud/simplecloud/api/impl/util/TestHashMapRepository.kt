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

package app.simplecloud.simplecloud.api.impl.util

import app.simplecloud.simplecloud.api.future.completedFuture
import app.simplecloud.simplecloud.api.future.nonNull
import app.simplecloud.simplecloud.api.future.unitFuture
import app.simplecloud.simplecloud.api.repository.Repository
import com.google.common.collect.Maps
import java.util.concurrent.CompletableFuture

/**
 * Date: 23.03.22
 * Time: 09:13
 * @author Frederick Baier
 *
 */
open class TestHashMapRepository<I : Any, T : Any> : Repository<I, T> {

    private val map = Maps.newConcurrentMap<I, T>()

    override fun findAll(): CompletableFuture<List<T>> {
        return completedFuture(this.map.values.toList())
    }

    override fun findFirst(): CompletableFuture<T> {
        return completedFuture(this.map.values.first())
    }

    override fun find(identifier: I): CompletableFuture<T> {
        return completedFuture(this.map[identifier]).nonNull(NoSuchElementException())
    }

    override fun findOrNull(identifier: I): CompletableFuture<T?> {
        return completedFuture(this.map[identifier])
    }

    override fun save(identifier: I, value: T): CompletableFuture<Unit> {
        this.map[identifier] = value
        return unitFuture()
    }

    override fun remove(identifier: I) {
        this.map.remove(identifier)
    }

    override fun count(): CompletableFuture<Long> {
        return completedFuture(this.map.size.toLong())
    }
}