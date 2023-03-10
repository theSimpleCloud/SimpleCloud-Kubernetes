package app.simplecloud.simplecloud.api.resourcedefinition

import app.simplecloud.simplecloud.api.resourcedefinition.limitation.Limitation

/**
 * Date: 14.01.23
 * Time: 18:15
 * @author Frederick Baier
 *
 */
class Definition(
    val type: String,
    val isOptional: Boolean,
    val limitations: List<Limitation>,
    val properties: Map<String, Definition>,
)