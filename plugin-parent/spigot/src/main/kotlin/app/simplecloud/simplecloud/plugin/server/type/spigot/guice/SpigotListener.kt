/*
 * SimpleCloud is a software for administrating a minecraft server network.
 * Copyright (C) 2022 Frederick Baier & Philipp Eistrach
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package app.simplecloud.simplecloud.plugin.server.type.spigot.guice

import app.simplecloud.simplecloud.plugin.OnlineCountUpdater
import com.google.inject.Inject
import kotlinx.coroutines.runBlocking
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * Date: 31.03.22
 * Time: 12:38
 * @author Frederick Baier
 *
 */
class SpigotListener @Inject constructor(
    private val onlineCountUpdater: OnlineCountUpdater
) : Listener {

    @EventHandler
    fun handleJoin(event: PlayerJoinEvent) {
        runBlocking {
            onlineCountUpdater.updateSelfOnlineCount()
        }
    }

    @EventHandler
    fun handleJoin(event: PlayerQuitEvent) {
        runBlocking {
            onlineCountUpdater.updateSelfOnlineCount(-1)
        }
    }

    @EventHandler
    fun handleJoin(event: PlayerKickEvent) {
        runBlocking {
            onlineCountUpdater.updateSelfOnlineCount(-1)
        }
    }

}