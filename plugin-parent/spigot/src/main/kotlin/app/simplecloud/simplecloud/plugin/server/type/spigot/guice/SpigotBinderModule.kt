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
import app.simplecloud.simplecloud.plugin.server.type.spigot.SpigotOnlineCountUpdater
import com.google.inject.AbstractModule
import org.bukkit.Server

/**
 * Date: 24.01.22
 * Time: 19:04
 * @author Frederick Baier
 *
 */
class SpigotBinderModule(
    private val server: Server
) : AbstractModule() {

    override fun configure() {
        bind(Server::class.java).toInstance(this.server)
        bind(OnlineCountUpdater::class.java).to(SpigotOnlineCountUpdater::class.java)
    }

}