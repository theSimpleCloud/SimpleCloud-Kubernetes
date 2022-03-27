package app.simplecloud.simplecloud.node.repository.mongo.onlinecountstrategy

import app.simplecloud.simplecloud.api.process.onlinestrategy.configuration.ProcessOnlineCountStrategyConfiguration
import dev.morphia.annotations.Entity
import dev.morphia.annotations.Id

/**
 * Date: 25.03.22
 * Time: 09:28
 * @author Frederick Baier
 *
 */
@Entity("online_count_strategies")
class OnlineCountStrategyEntity(
    @Id
    val name: String,
    val className: String,
    val targetGroupNames: Set<String>,
    val data: Map<String, String>
) {

    private constructor() : this("", "", emptySet(), emptyMap())

    fun toConfiguration(): ProcessOnlineCountStrategyConfiguration {
        return ProcessOnlineCountStrategyConfiguration(
            this.name,
            this.className,
            this.targetGroupNames,
            this.data
        )
    }

    companion object {

        fun fromConfiguration(configuration: ProcessOnlineCountStrategyConfiguration): OnlineCountStrategyEntity {
            return OnlineCountStrategyEntity(
                configuration.name,
                configuration.className,
                configuration.targetGroupNames,
                configuration.data
            )
        }

    }

}