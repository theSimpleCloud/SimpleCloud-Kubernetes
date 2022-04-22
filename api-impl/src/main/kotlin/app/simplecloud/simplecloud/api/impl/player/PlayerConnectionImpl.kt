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

package app.simplecloud.simplecloud.api.impl.player

import app.simplecloud.simplecloud.api.player.PlayerConnection
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.distribution.api.Address
import java.util.*

/**
 * Date: 07.01.22
 * Time: 18:00
 * @author Frederick Baier
 *
 */
class PlayerConnectionImpl(
    private val configuration: PlayerConnectionConfiguration
) : PlayerConnection {

    override fun getUniqueId(): UUID {
        return this.configuration.uniqueId
    }

    override fun getAddress(): Address {
        return this.configuration.address
    }

    override fun isOnlineMode(): Boolean {
        return this.configuration.onlineMode
    }

    override fun getVersion(): Int {
        return this.configuration.numericalClientVersion
    }

    override fun getName(): String {
        return this.configuration.name
    }

    override fun toConfiguration(): PlayerConnectionConfiguration {
        return this.configuration
    }
}