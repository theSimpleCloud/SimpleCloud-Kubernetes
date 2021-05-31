package eu.thesimplecloud.api.impl.repository

import eu.thesimplecloud.api.impl.future.nonNull
import eu.thesimplecloud.api.process.ICloudProcess
import eu.thesimplecloud.api.repository.IIdentifiable
import eu.thesimplecloud.api.repository.IRepository
import org.apache.ignite.IgniteCache
import org.apache.ignite.cache.query.Query
import org.apache.ignite.cache.query.ScanQuery
import org.apache.ignite.lang.IgniteBiPredicate
import java.util.concurrent.CompletableFuture
import javax.cache.Cache

/**
 * Created by IntelliJ IDEA.
 * Date: 21.04.2021
 * Time: 19:09
 * @author Frederick Baier
 */
abstract class AbstractIgniteRepository<T : IIdentifiable<String>> : IRepository<String, T> {

    abstract fun getCache(): IgniteCache<String, T>

    override fun findAll(): CompletableFuture<List<T>> {
        return CompletableFuture.supplyAsync { getCache().toList().map { it.value } }
    }

    override fun find(identifier: String): CompletableFuture<T> {
        return CompletableFuture.supplyAsync { getCache().get(identifier) }.nonNull()
    }

    override fun save(value: T) {
        getCache().putAsync(value.getIdentifier(), value)
    }

    protected fun executeQuery(predicate: IgniteBiPredicate<String, T>): CompletableFuture<List<T>> {
        return CompletableFuture.supplyAsync {
            val cursor = getCache().query(ScanQuery(predicate))
            return@supplyAsync cursor.all.map { it.value }
        }.nonNull()
    }

    protected fun executeQueryAndFindFirst(predicate: IgniteBiPredicate<String, T>): CompletableFuture<T> {
        return executeQuery(predicate).thenApply { it.first() }
    }

}