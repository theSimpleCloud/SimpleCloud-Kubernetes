package app.simplecloud.simplecloud.api.permission.configuration

import java.util.*

/**
 * Date: 19.03.22
 * Time: 20:56
 * @author Frederick Baier
 *
 */
class PermissionPlayerConfiguration(
    val uniqueId: UUID,
    val permissions: List<PermissionConfiguration>
) {

    constructor() : this(UUID.randomUUID(), emptyList())

}