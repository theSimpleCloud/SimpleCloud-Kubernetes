package app.simplecloud.simplecloud.api.player.message

import net.kyori.adventure.text.Component
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 21.10.22
 * Time: 09:35
 */
data class ActionBarConfiguration(
    val uniqueId: UUID,
    val message: Component,
)