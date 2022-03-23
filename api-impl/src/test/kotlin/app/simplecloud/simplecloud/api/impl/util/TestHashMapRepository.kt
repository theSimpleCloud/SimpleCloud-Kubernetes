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