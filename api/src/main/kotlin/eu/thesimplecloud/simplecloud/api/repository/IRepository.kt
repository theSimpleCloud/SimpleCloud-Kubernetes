package eu.thesimplecloud.simplecloud.api.repository

import eu.thesimplecloud.simplecloud.api.utils.IIdentifiable
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
interface IRepository<I : Any, T : IIdentifiable<I>> {

    /**
     * Returns all values stored
     */
    fun findAll(): CompletableFuture<List<T>>

    /**
     * Returns the object found by the specified [identifier]
     */
    fun find(identifier: I): CompletableFuture<T>

    /**
     * Saves the specified [value] and replaces it if needed according to its identifier
     */
    fun put(value: T)

    /**
     * Removes the value found by the specified [identifier]
     */
    fun remove(identifier: I)

    /**
     * Removes the value found by [value]s identifier
     */
    fun remove(value: T) {
        remove(value.getIdentifier())
    }

}