package app.simplecloud.simplecloud.node.repository.mongo.strategymap

import dev.morphia.annotations.Entity
import dev.morphia.annotations.Id

/**
 * Date: 25.03.22
 * Time: 09:28
 * @author Frederick Baier
 *
 */
@Entity("online_count_strategy_map")
class OnlineCountStrategyMapEntity(
    @Id
    val groupName: String,
    val onlineStrategyName: String
)