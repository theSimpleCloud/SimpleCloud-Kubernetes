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

package app.simplecloud.simplecloud.plugin.proxy.type.bungee

import app.simplecloud.simplecloud.plugin.OnlineCountProvider
import com.google.inject.Inject
import com.google.inject.Singleton
import net.md_5.bungee.api.ProxyServer

/**
 * Date: 23.01.22
 * Time: 19:13
 * @author Frederick Baier
 *
 */
@Singleton
class BungeeOnlineCountProvider @Inject constructor(
    private val proxyServer: ProxyServer
) : OnlineCountProvider {

    override fun getOnlineCount(): Int {
        return proxyServer.onlineCount
    }

}