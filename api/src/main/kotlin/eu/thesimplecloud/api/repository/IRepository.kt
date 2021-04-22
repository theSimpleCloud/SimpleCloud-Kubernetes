package eu.thesimplecloud.api.repository

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
    fun save(value: T)

}