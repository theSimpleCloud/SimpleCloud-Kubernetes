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

package app.simplecloud.simplecloud.plugin.proxy.request.handler

import app.simplecloud.simplecloud.api.impl.repository.ignite.IgniteCloudPlayerRepository
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.plugin.startup.SelfProcessProvider
import app.simplecloud.simplecloud.plugin.util.OnlineCountProvider

class PlayerDisconnectRequestHandler(
    private val connection: PlayerConnectionConfiguration,
    private val selfProcessProvider: SelfProcessProvider,
    private val onlineCountProvider: OnlineCountProvider,
    private val igniteCloudPlayerRepository: IgniteCloudPlayerRepository
) {
    fun handler() {
        this.igniteCloudPlayerRepository.remove(connection.uniqueId)
        OnlineCountUpdater(selfProcessProvider, onlineCountProvider).updateOnlineCount()
    }

}
