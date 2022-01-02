package app.simplecloud.simplecloud.api.reference

import java.util.concurrent.CompletableFuture

/**
 * Used to reference objects.
 * So only an identifier is transmitted and the objects itself can be resolved when needed
 * @param T The type of the object
 */
interface Reference<T : Any> {

    /**
     * Resolves the object
     * @return a promise that completes when the result is ready or fails when there was an error resolving the object
     */
    fun resolveReference(): CompletableFuture<T>

}