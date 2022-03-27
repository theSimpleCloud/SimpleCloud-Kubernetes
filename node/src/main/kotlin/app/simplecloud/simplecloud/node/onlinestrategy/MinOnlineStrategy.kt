package app.simplecloud.simplecloud.node.onlinestrategy

import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.process.onlinestrategy.ProcessesOnlineCountStrategy
import app.simplecloud.simplecloud.api.process.onlinestrategy.configuration.ProcessOnlineCountStrategyConfiguration

/**
 * Date: 27.03.22
 * Time: 11:23
 * @author Frederick Baier
 *
 */
class MinOnlineStrategy(
    private val configuration: ProcessOnlineCountStrategyConfiguration
) : ProcessesOnlineCountStrategy {

    private val minCount = configuration.data["min"]!!.toInt()

    override fun getName(): String {
        return this.configuration.name
    }

    override fun getTargetGroupNames(): Set<String> {
        return this.configuration.targetGroupNames
    }

    override fun calculateOnlineCount(group: CloudProcessGroup): Int {
        return this.minCount
    }

    override fun toConfiguration(): ProcessOnlineCountStrategyConfiguration {
        return this.configuration
    }


    class Factory : ProcessesOnlineCountStrategy.Factory {

        override fun create(configuration: ProcessOnlineCountStrategyConfiguration): ProcessesOnlineCountStrategy {
            return MinOnlineStrategy(configuration)
        }

    }

}