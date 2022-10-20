package app.simplecloud.simplecloud.api.player.message

import net.kyori.adventure.audience.MessageType
import net.kyori.adventure.text.Component
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 19.10.22
 * Time: 19:22
 */
data class MessageConfiguration(
    val uniqueId: UUID,
    val message: Component,
    val type: MessageType
)