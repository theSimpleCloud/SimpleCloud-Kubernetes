package app.simplecloud.simplecloud.api.internal.service

import app.simplecloud.simplecloud.api.process.onlinestrategy.ProcessesOnlineCountStrategy
import app.simplecloud.simplecloud.api.process.onlinestrategy.configuration.ProcessOnlineCountStrategyConfiguration
import app.simplecloud.simplecloud.api.service.NodeProcessOnlineStrategyService

/**
 * Date: 27.03.22
 * Time: 10:10
 * @author Frederick Baier
 *
 */
interface InternalNodeProcessOnlineCountStrategyService : NodeProcessOnlineStrategyService {

    suspend fun createStrategyInternal(configuration: ProcessOnlineCountStrategyConfiguration): ProcessesOnlineCountStrategy

    suspend fun deleteStrategyInternal(strategy: ProcessesOnlineCountStrategy)

    suspend fun updateStrategyInternal(configuration: ProcessOnlineCountStrategyConfiguration)


}