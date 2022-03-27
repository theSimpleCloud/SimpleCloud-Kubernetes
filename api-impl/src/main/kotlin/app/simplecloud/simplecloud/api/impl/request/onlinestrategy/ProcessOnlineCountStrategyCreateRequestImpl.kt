package app.simplecloud.simplecloud.api.impl.request.onlinestrategy

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.api.internal.service.InternalNodeProcessOnlineCountStrategyService
import app.simplecloud.simplecloud.api.process.onlinestrategy.ProcessesOnlineCountStrategy
import app.simplecloud.simplecloud.api.process.onlinestrategy.configuration.ProcessOnlineCountStrategyConfiguration
import app.simplecloud.simplecloud.api.request.onlinestrategy.ProcessOnlineCountStrategyCreateRequest
import com.google.inject.Inject
import java.util.concurrent.CompletableFuture

/**
 * Date: 27.03.22
 * Time: 10:08
 * @author Frederick Baier
 *
 */
class ProcessOnlineCountStrategyCreateRequestImpl @Inject constructor(
    private val configuration: ProcessOnlineCountStrategyConfiguration,
    private val internalService: InternalNodeProcessOnlineCountStrategyService
) : ProcessOnlineCountStrategyCreateRequest {

    override fun submit(): CompletableFuture<ProcessesOnlineCountStrategy> = CloudScope.future {
        if (doesStrategyExist(configuration.name)) {
            throw IllegalArgumentException("Strategy already exists")
        }
        if (!doesClassExist(configuration.className)) {
            throw IllegalArgumentException("Class '${configuration.className}' does not exist")
        }
        return@future internalService.createStrategyInternal(configuration)
    }

    private fun doesClassExist(className: String): Boolean {
        return try {
            Class.forName(className)
            true
        } catch (ex: ClassNotFoundException) {
            false
        }
    }

    private suspend fun doesStrategyExist(strategyName: String): Boolean {
        return try {
            this.internalService.findByName(strategyName).await()
            true
        } catch (e: NoSuchElementException) {
            false
        }
    }
}