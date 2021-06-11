package eu.thesimplecloud.simplecloud.api.utils

import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 17.03.2021
 * Time: 17:12
 * @author Frederick Baier
 *
 * Represents a request
 * The request is filled with information and then submitted
 *
 */
interface IRequest<T : Any> {

    /**
     * Submits the request
     * @return a promise completing with the result of the request
     */
    fun submit(): CompletableFuture<T>

}