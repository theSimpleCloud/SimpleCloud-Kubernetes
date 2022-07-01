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

package app.simplecloud.simplecloud.plugin.server.type.spigot

import app.simplecloud.simplecloud.api.impl.env.RealEnvironmentVariables
import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.distribution.hazelcast.HazelcastDistributionFactory
import app.simplecloud.simplecloud.plugin.SelfOnlineCountProvider
import app.simplecloud.simplecloud.plugin.server.CloudServerPlugin
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

/**
 * Date: 31.03.22
 * Time: 12:23
 * @author Frederick Baier
 *
 */
class CloudSpigotPlugin : JavaPlugin() {

    private val cloudPlugin = CloudServerPlugin(
        HazelcastDistributionFactory(),
        RealEnvironmentVariables(),
        Address.fromIpString("distribution:1670"),
        SelfOnlineCountProvider { Bukkit.getOnlinePlayers().size }
    )

    override fun onEnable() {
        server.pluginManager.registerEvents(SpigotListener(this.cloudPlugin.onlineCountUpdater), this)
    }

    override fun onDisable() {

    }

}