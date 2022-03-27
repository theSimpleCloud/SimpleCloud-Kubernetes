package app.simplecloud.simplecloud.node.onlinestrategy

import app.simplecloud.simplecloud.api.process.onlinestrategy.ProcessesOnlineCountStrategy
import app.simplecloud.simplecloud.api.process.onlinestrategy.configuration.ProcessOnlineCountStrategyConfiguration
import com.google.inject.Singleton

/**
 * Date: 26.03.22
 * Time: 09:07
 * @author Frederick Baier
 *
 */
@Singleton
class UniversalProcessOnlineCountStrategyFactory : ProcessesOnlineCountStrategy.Factory {

    override fun create(configuration: ProcessOnlineCountStrategyConfiguration): ProcessesOnlineCountStrategy {
        val className = configuration.className
        val factoryClassName = "$className\$Factory"
        val factoryClass = Class.forName(factoryClassName).asSubclass(ProcessesOnlineCountStrategy.Factory::class.java)
        val factoryInstance = factoryClass.getDeclaredConstructor().newInstance()
        return factoryInstance.create(configuration)
    }

}