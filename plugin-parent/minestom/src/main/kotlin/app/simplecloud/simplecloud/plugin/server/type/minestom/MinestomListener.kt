package app.simplecloud.simplecloud.plugin.server.type.minestom

import app.simplecloud.simplecloud.plugin.OnlineCountUpdater
import kotlinx.coroutines.runBlocking
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerLoginEvent

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 01.07.22
 * Time: 18:43
 */
class MinestomListener(
    private val onlineCountUpdater: OnlineCountUpdater,
    private val eventNode: EventNode<Event>
) {

    fun listen() {
        this.eventNode
            .addListener(PlayerLoginEvent::class.java) {
                runBlocking {
                    onlineCountUpdater.updateSelfOnlineCount()
                }
            }
            .addListener(PlayerDisconnectEvent::class.java) {
                runBlocking {
                    onlineCountUpdater.updateSelfOnlineCount(-1)
                }
            }
    }

}