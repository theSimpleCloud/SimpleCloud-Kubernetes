package app.simplecloud.simplecloud.api.service

import app.simplecloud.simplecloud.api.process.onlinestrategy.ProcessesOnlineCountStrategy
import app.simplecloud.simplecloud.api.process.onlinestrategy.configuration.ProcessOnlineCountStrategyConfiguration
import app.simplecloud.simplecloud.api.request.onlinestrategy.ProcessOnlineCountStrategyCreateRequest
import app.simplecloud.simplecloud.api.request.onlinestrategy.ProcessOnlineCountStrategyDeleteRequest
import app.simplecloud.simplecloud.api.request.onlinestrategy.ProcessOnlineCountStrategyUpdateRequest
import java.util.concurrent.CompletableFuture

/**
 * Date: 25.03.22
 * Time: 08:43
 * @author Frederick Baier
 *
 */
interface NodeProcessOnlineStrategyService {

    /**
     * Starts and stops processes by comparing the current number of online processes to the desired one
     */
    fun checkProcessOnlineCount()

    /**
     * Returns the strategy found by the specified [name]
     */
    fun findByName(name: String): CompletableFuture<ProcessesOnlineCountStrategy>

    /**
     * Returns all available strategies
     */
    fun findAll(): CompletableFuture<List<ProcessesOnlineCountStrategy>>

    /**
     * Returns the [ProcessesOnlineCountStrategy] found by the specified [name] or a default config
     */
    fun findByProcessGroupName(name: String): CompletableFuture<ProcessesOnlineCountStrategy>

    /**
     * Returns a request to create a new [ProcessesOnlineCountStrategy]
     */
    fun createCreateRequest(configuration: ProcessOnlineCountStrategyConfiguration): ProcessOnlineCountStrategyCreateRequest

    /**
     * Returns a request to update a [ProcessesOnlineCountStrategy]
     */
    fun createUpdateRequest(strategy: ProcessesOnlineCountStrategy): ProcessOnlineCountStrategyUpdateRequest

    /**
     * Returns a request to delete the specified [strategy]
     */
    fun createDeleteRequest(strategy: ProcessesOnlineCountStrategy): ProcessOnlineCountStrategyDeleteRequest

}