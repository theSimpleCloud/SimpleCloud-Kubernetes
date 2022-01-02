package app.simplecloud.simplecloud.api.repository

import app.simplecloud.simplecloud.api.future.isCompletedNormally
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 22.03.2021
 * Time: 18:32
 * @author Frederick Baier
 *
 *  @param I identifier
 *  @param T type to be stored
 */
interface Repository<I : Any, T : Any> {

    /**
     * Returns all values stored
     */
    fun findAll(): CompletableFuture<List<T>>

    /**
     * Returns the object found by the specified [identifier]
     */
    fun find(identifier: I): CompletableFuture<T>

    /**
     * Returns the object found by the specified [identifier] or null
     */
    fun findOrNull(identifier: I): CompletableFuture<T?>

    /**
     * Saves the specified [value] and replaces it if needed according to its identifier
     */
    fun save(identifier: I, value: T): CompletableFuture<Unit>

    /**
     * Removes the value found by the specified [identifier]
     */
    fun remove(identifier: I)

    /**
     * Checks whether the specified [identifier] exists
     */
    fun doesExist(identifier: I): CompletableFuture<Boolean> {
        val future = find(identifier)
        return future.handle { _, _ -> future.isCompletedNormally }
    }

    /**
     * Returns the count of elements stored in this repository
     */
    fun count(): CompletableFuture<Long>

}