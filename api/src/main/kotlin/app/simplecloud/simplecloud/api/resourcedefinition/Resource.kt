package app.simplecloud.simplecloud.api.resourcedefinition

/**
 * Date: 14.01.23
 * Time: 18:52
 * @author Frederick Baier
 *
 */
class Resource(
    val apiVersion: String,
    val kind: String,
    val name: String,
    val spec: Map<String, Any>,
)