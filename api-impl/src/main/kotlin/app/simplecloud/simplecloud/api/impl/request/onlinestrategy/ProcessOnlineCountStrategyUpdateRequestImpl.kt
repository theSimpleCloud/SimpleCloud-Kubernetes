package app.simplecloud.simplecloud.api.impl.request.onlinestrategy

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.api.internal.service.InternalNodeProcessOnlineCountStrategyService
import app.simplecloud.simplecloud.api.process.onlinestrategy.ProcessesOnlineCountStrategy
import app.simplecloud.simplecloud.api.process.onlinestrategy.configuration.ProcessOnlineCountStrategyConfiguration
import app.simplecloud.simplecloud.api.request.onlinestrategy.ProcessOnlineCountStrategyUpdateRequest
import com.google.inject.Inject
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Date: 27.03.22
 * Time: 10:08
 * @author Frederick Baier
 *
 */
class ProcessOnlineCountStrategyUpdateRequestImpl @Inject constructor(
    private val strategy: ProcessesOnlineCountStrategy,
    private val internalService: InternalNodeProcessOnlineCountStrategyService
) : ProcessOnlineCountStrategyUpdateRequest {

    private val targetGroupNames = CopyOnWriteArraySet(this.strategy.getTargetGroupNames())


    @Volatile
    private var data = this.strategy.toConfiguration().data

    override fun getStrategy(): ProcessesOnlineCountStrategy {
        return this.strategy
    }

    override fun clearTargetGroups(): ProcessOnlineCountStrategyUpdateRequest {
        this.targetGroupNames.clear()
        return this
    }

    override fun addTargetGroup(name: String): ProcessOnlineCountStrategyUpdateRequest {
        this.targetGroupNames.add(name)
        return this
    }

    override fun removeTargetGroup(name: String): ProcessOnlineCountStrategyUpdateRequest {
        this.targetGroupNames.remove(name)
        return this
    }

    override fun setData(data: Map<String, String>): ProcessOnlineCountStrategyUpdateRequest {
        this.data = data
        return this
    }

    override fun submit(): CompletableFuture<Unit> = CloudScope.future {
        val configuration = ProcessOnlineCountStrategyConfiguration(
            strategy.getName(),
            strategy.toConfiguration().className,
            targetGroupNames,
            data
        )
        internalService.updateStrategyInternal(configuration)
    }
}