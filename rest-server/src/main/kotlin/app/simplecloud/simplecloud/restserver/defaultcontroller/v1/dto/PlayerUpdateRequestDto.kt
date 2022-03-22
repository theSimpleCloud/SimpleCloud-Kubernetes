package app.simplecloud.simplecloud.restserver.defaultcontroller.v1.dto

import app.simplecloud.simplecloud.api.permission.configuration.PermissionPlayerConfiguration
import app.simplecloud.simplecloud.api.player.PlayerWebConfig
import java.util.*

/**
 * Date: 22.03.22
 * Time: 12:25
 * @author Frederick Baier
 *
 */
class PlayerUpdateRequestDto(
    val uniqueId: UUID,
    val displayName: String,
    val webConfig: PlayerWebConfig,
    val permissionPlayerConfiguration: PermissionPlayerConfiguration
) {

    private constructor() : this(
        UUID.randomUUID(),
        "",
        PlayerWebConfig("", false),
        PermissionPlayerConfiguration(UUID.randomUUID(), emptyList())
    )

}