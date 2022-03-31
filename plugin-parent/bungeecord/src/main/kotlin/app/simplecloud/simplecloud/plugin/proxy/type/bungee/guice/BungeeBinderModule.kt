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

package app.simplecloud.simplecloud.plugin.proxy.type.bungee.guice

import app.simplecloud.simplecloud.plugin.OnlineCountUpdater
import app.simplecloud.simplecloud.plugin.proxy.ProxyServerRegistry
import app.simplecloud.simplecloud.plugin.proxy.type.bungee.BungeeOnlineCountUpdater
import app.simplecloud.simplecloud.plugin.proxy.type.bungee.BungeeProxyServerRegistry
import com.google.inject.AbstractModule
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin

/**
 * Date: 24.01.22
 * Time: 19:04
 * @author Frederick Baier
 *
 */
class BungeeBinderModule(
    private val plugin: Plugin
) : AbstractModule() {

    override fun configure() {
        bind(Plugin::class.java).toInstance(plugin)
        bind(ProxyServer::class.java).toInstance(ProxyServer.getInstance())
        bind(ProxyServerRegistry::class.java).to(BungeeProxyServerRegistry::class.java)
        bind(OnlineCountUpdater::class.java).to(BungeeOnlineCountUpdater::class.java)
    }

}