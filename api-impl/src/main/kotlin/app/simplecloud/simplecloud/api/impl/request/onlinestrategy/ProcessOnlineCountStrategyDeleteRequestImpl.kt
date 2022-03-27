package app.simplecloud.simplecloud.api.impl.request.onlinestrategy

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.api.internal.service.InternalNodeProcessOnlineCountStrategyService
import app.simplecloud.simplecloud.api.process.onlinestrategy.ProcessesOnlineCountStrategy
import app.simplecloud.simplecloud.api.request.onlinestrategy.ProcessOnlineCountStrategyDeleteRequest
import com.google.inject.Inject
import java.util.concurrent.CompletableFuture

/**
 * Date: 27.03.22
 * Time: 10:08
 * @author Frederick Baier
 *
 */
class ProcessOnlineCountStrategyDeleteRequestImpl @Inject constructor(
    private val strategy: ProcessesOnlineCountStrategy,
    private val internalService: InternalNodeProcessOnlineCountStrategyService
) : ProcessOnlineCountStrategyDeleteRequest {

    override fun getStrategy(): ProcessesOnlineCountStrategy {
        return this.strategy
    }

    override fun submit(): CompletableFuture<Unit> = CloudScope.future {
        internalService.deleteStrategyInternal(strategy)
    }
}