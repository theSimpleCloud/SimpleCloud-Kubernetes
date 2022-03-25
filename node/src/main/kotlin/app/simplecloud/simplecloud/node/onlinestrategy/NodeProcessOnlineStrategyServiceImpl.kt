package app.simplecloud.simplecloud.node.onlinestrategy

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.api.future.unpackFutureException
import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.process.onlineonfiguration.ProcessesOnlineCountStrategy
import app.simplecloud.simplecloud.node.repository.ignite.IgniteOnlineCountStrategyMapRepository
import app.simplecloud.simplecloud.node.task.NodeOnlineProcessesChecker
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.Singleton
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture

/**
 * Date: 25.03.22
 * Time: 08:44
 * @author Frederick Baier
 *
 */
@Singleton
class NodeProcessOnlineStrategyServiceImpl @Inject constructor(
    private val injector: Injector,
    private val igniteRepository: IgniteOnlineCountStrategyMapRepository,
    private val registry: NodeProcessOnlineStrategyRegistry
) : NodeProcessOnlineStrategyService {

    private val onlineProcessesChecker by lazy { this.injector.getInstance(NodeOnlineProcessesChecker::class.java) }

    override fun checkProcessOnlineCount() {
        CloudScope.launch {
            onlineProcessesChecker.checkOnlineCount()
        }
    }

    override fun getByProcessGroupName(name: String): CompletableFuture<ProcessesOnlineCountStrategy> =
        CloudScope.future {
            return@future try {
                getByProcessGroupName0(name)
            } catch (ex: Exception) {
                val unpackedException = unpackFutureException(ex)
                if (unpackedException !is NoSuchElementException)
                    throw unpackedException
                DefaultProcessesOnlineCountStrategy
            }
        }

    private suspend fun getByProcessGroupName0(name: String): ProcessesOnlineCountStrategy {
        val onlineCountStrategyName = this.igniteRepository.find(name).await()
        return this.registry.getStrategyByName(onlineCountStrategyName)
    }


    object DefaultProcessesOnlineCountStrategy : ProcessesOnlineCountStrategy {

        override fun calculateOnlineCount(group: CloudProcessGroup): Int {
            return 0
        }

        override fun getName(): String {
            return "<default>"
        }

    }

}