package app.simplecloud.simplecloud.api.player.configuration

import app.simplecloud.simplecloud.api.utils.Address
import java.util.*

/**
 * Date: 07.01.22
 * Time: 00:27
 * @author Frederick Baier
 *
 */
class PlayerConnectionConfiguration(
    val uniqueId: UUID,
    val numericalClientVersion: Int,
    val name: String,
    val address: Address,
    val onlineMode: Boolean,
) {

    constructor() : this(
        UUID.randomUUID(),
        -1,
        "",
        Address("", -1),
        false
    )

}