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

package app.simplecloud.simplecloud.api.player

import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.api.utils.Address
import app.simplecloud.simplecloud.api.utils.Nameable
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * Date: 21.03.2021
 * Time: 15:33
 * @author Frederick Baier
 */
interface PlayerConnection : Nameable {

    /**
     * Returns the unique id of this player
     */
    fun getUniqueId(): UUID

    /**
     * Returns the address of this player
     */
    fun getAddress(): Address

    /**
     * Returns whether the player's connection is in online mode
     */
    fun isOnlineMode(): Boolean

    /**
     * Get the numerical client version of the player
     * @return the protocol version of the client
     */
    fun getVersion(): Int

    /**
     * Returns the configuration of this connection
     */
    fun toConfiguration(): PlayerConnectionConfiguration

}