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

import app.simplecloud.simplecloud.distibution.hazelcast.HazelcastDistributionFactory
import app.simplecloud.simplecloud.plugin.startup.CloudPlugin
import app.simplecloud.simplecloud.plugin.startup.SelfProcessProvider
import org.bukkit.plugin.java.JavaPlugin

/**
 * Date: 31.03.22
 * Time: 12:23
 * @author Frederick Baier
 *
 */
class CloudSpigotPlugin : JavaPlugin() {

    private val cloudPlugin = CloudPlugin(HazelcastDistributionFactory())
    private val cloudAPI = cloudPlugin.pluginCloudAPI

    override fun onEnable() {
        val onlineCountUpdater = SpigotOnlineCountUpdater(
            this.server,
            SelfProcessProvider(this.cloudPlugin.selfProcessId, this.cloudAPI.getProcessService())
        )
        server.pluginManager.registerEvents(SpigotListener(onlineCountUpdater), this)
    }

    override fun onDisable() {

    }

}