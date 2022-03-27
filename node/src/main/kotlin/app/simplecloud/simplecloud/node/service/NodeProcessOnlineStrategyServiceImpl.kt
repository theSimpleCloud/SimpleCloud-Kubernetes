package app.simplecloud.simplecloud.node.service

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.api.impl.request.onlinestrategy.ProcessOnlineCountStrategyCreateRequestImpl
import app.simplecloud.simplecloud.api.impl.request.onlinestrategy.ProcessOnlineCountStrategyDeleteRequestImpl
import app.simplecloud.simplecloud.api.impl.request.onlinestrategy.ProcessOnlineCountStrategyUpdateRequestImpl
import app.simplecloud.simplecloud.api.internal.service.InternalNodeProcessOnlineCountStrategyService
import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.process.onlinestrategy.ProcessesOnlineCountStrategy
import app.simplecloud.simplecloud.api.process.onlinestrategy.configuration.ProcessOnlineCountStrategyConfiguration
import app.simplecloud.simplecloud.api.request.onlinestrategy.ProcessOnlineCountStrategyCreateRequest
import app.simplecloud.simplecloud.api.request.onlinestrategy.ProcessOnlineCountStrategyDeleteRequest
import app.simplecloud.simplecloud.api.request.onlinestrategy.ProcessOnlineCountStrategyUpdateRequest
import app.simplecloud.simplecloud.node.onlinestrategy.UniversalProcessOnlineCountStrategyFactory
import app.simplecloud.simplecloud.node.repository.ignite.IgniteOnlineCountStrategyRepository
import app.simplecloud.simplecloud.node.repository.mongo.onlinecountstrategy.MongoOnlineCountStrategyRepository
import app.simplecloud.simplecloud.node.repository.mongo.onlinecountstrategy.OnlineCountStrategyEntity
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
    private val igniteRepository: IgniteOnlineCountStrategyRepository,
    private val mongoRepository: MongoOnlineCountStrategyRepository,
    private val factory: UniversalProcessOnlineCountStrategyFactory
) : InternalNodeProcessOnlineCountStrategyService {

    private val onlineProcessesChecker by lazy { this.injector.getInstance(NodeOnlineProcessesChecker::class.java) }

    override fun checkProcessOnlineCount() {
        CloudScope.launch {
            onlineProcessesChecker.checkOnlineCount()
        }
    }

    override fun findByName(name: String): CompletableFuture<ProcessesOnlineCountStrategy> = CloudScope.future {
        val configuration = igniteRepository.find(name).await()
        return@future factory.create(configuration)
    }

    override fun findAll(): CompletableFuture<List<ProcessesOnlineCountStrategy>> = CloudScope.future {
        val configurations = igniteRepository.findAll().await()
        return@future configurations.map { factory.create(it) }
    }

    override fun findByProcessGroupName(
        name: String
    ): CompletableFuture<ProcessesOnlineCountStrategy> = CloudScope.future {
        val foundStrategies = igniteRepository.findByTargetProcessGroup(name).await()
        if (foundStrategies.isEmpty()) return@future DefaultProcessesOnlineCountStrategy
        return@future factory.create(foundStrategies.first())
    }

    override fun createCreateRequest(configuration: ProcessOnlineCountStrategyConfiguration): ProcessOnlineCountStrategyCreateRequest {
        return ProcessOnlineCountStrategyCreateRequestImpl(configuration, this)
    }

    override fun createUpdateRequest(strategy: ProcessesOnlineCountStrategy): ProcessOnlineCountStrategyUpdateRequest {
        return ProcessOnlineCountStrategyUpdateRequestImpl(strategy, this)
    }

    override fun createDeleteRequest(strategy: ProcessesOnlineCountStrategy): ProcessOnlineCountStrategyDeleteRequest {
        return ProcessOnlineCountStrategyDeleteRequestImpl(strategy, this)
    }

    override suspend fun createStrategyInternal(configuration: ProcessOnlineCountStrategyConfiguration): ProcessesOnlineCountStrategy {
        val permissionGroup = this.factory.create(configuration)
        updateStrategyInternal(configuration)
        return permissionGroup
    }

    override suspend fun updateStrategyInternal(configuration: ProcessOnlineCountStrategyConfiguration) {
        this.igniteRepository.save(configuration.name, configuration).await()
        saveToDatabase(configuration)
        checkProcessOnlineCount()
    }

    override suspend fun deleteStrategyInternal(strategy: ProcessesOnlineCountStrategy) {
        this.igniteRepository.remove(strategy.getName())
        deleteStrategyFromDatabase(strategy)
        checkProcessOnlineCount()
    }

    private fun deleteStrategyFromDatabase(strategy: ProcessesOnlineCountStrategy) {
        this.mongoRepository.remove(strategy.getName())
    }

    private fun saveToDatabase(configuration: ProcessOnlineCountStrategyConfiguration) {
        val entity = OnlineCountStrategyEntity.fromConfiguration(configuration)
        this.mongoRepository.save(configuration.name, entity)
    }

    object DefaultProcessesOnlineCountStrategy : ProcessesOnlineCountStrategy {

        override fun getTargetGroupNames(): Set<String> {
            return emptySet()
        }

        override fun calculateOnlineCount(group: CloudProcessGroup): Int {
            return 0
        }

        override fun getName(): String {
            return "<default>"
        }

        override fun toConfiguration(): ProcessOnlineCountStrategyConfiguration {
            return ProcessOnlineCountStrategyConfiguration(
                getName(),
                DefaultProcessesOnlineCountStrategy::class.java.name,
                getTargetGroupNames(),
                emptyMap()
            )
        }

    }

}