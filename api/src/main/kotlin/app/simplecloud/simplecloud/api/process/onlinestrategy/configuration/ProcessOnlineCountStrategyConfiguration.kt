package app.simplecloud.simplecloud.api.process.onlinestrategy.configuration

/**
 * Date: 26.03.22
 * Time: 08:53
 * @author Frederick Baier
 *
 */
class ProcessOnlineCountStrategyConfiguration(
    val name: String,
    val className: String,
    val targetGroupNames: Set<String>,
    val data: Map<String, String>
) {

    private constructor() : this("", "", emptySet(), emptyMap())

}