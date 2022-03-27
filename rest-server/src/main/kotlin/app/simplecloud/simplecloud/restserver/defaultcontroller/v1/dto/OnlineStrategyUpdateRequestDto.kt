package app.simplecloud.simplecloud.restserver.defaultcontroller.v1.dto

/**
 * Date: 27.03.22
 * Time: 09:59
 * @author Frederick Baier
 *
 */
class OnlineStrategyUpdateRequestDto(
    val name: String,
    val targetGroupNames: List<String>,
    val data: Map<String, String>
) {

    private constructor() : this("", emptyList(), emptyMap())

}