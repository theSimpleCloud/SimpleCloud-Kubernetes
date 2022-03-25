package app.simplecloud.simplecloud.node.onlinestrategy

import app.simplecloud.simplecloud.api.process.onlineonfiguration.ProcessesOnlineCountStrategy
import com.google.inject.Singleton
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Date: 03.02.22
 * Time: 21:52
 * @author Frederick Baier
 *
 */
@Singleton
class NodeProcessOnlineStrategyRegistryImpl : NodeProcessOnlineStrategyRegistry {

    private val registeredStrategies = CopyOnWriteArraySet<ProcessesOnlineCountStrategy>()

    override fun register(strategy: ProcessesOnlineCountStrategy) {
        this.registeredStrategies.add(strategy)
    }

    override fun unregister(strategy: ProcessesOnlineCountStrategy) {
        this.registeredStrategies.remove(strategy)
    }

    override fun getAvailableStrategies(): Collection<ProcessesOnlineCountStrategy> {
        return this.registeredStrategies
    }

    override fun getStrategyByName(name: String): ProcessesOnlineCountStrategy {
        return this.registeredStrategies.first { it.getName().equals(name, true) }
    }

}