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

import app.simplecloud.simplecloud.api.permission.PermissionPlayer
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import app.simplecloud.simplecloud.api.request.player.OfflineCloudPlayerUpdateRequest
import app.simplecloud.simplecloud.api.utils.Nameable

/**
 * Created by IntelliJ IDEA.
 * Date: 21.03.2021
 * Time: 15:32
 * @author Frederick Baier
 */
interface OfflineCloudPlayer : PermissionPlayer, Nameable {

    /**
     * Returns the timestamp the player first logged in
     */
    fun getFirstLogin(): Long

    /**
     * Returns the timestamp the player last logged in
     */
    fun getLastLogin(): Long

    /**
     * Returns the time in milliseconds the player was online
     */
    fun getOnlineTime(): Long

    /**
     * Returns the last [PlayerConnection] the player had
     */
    fun getLastPlayerConnection(): PlayerConnection

    /**
     * Returns whether the player is online
     */
    fun isOnline(): Boolean

    /**
     * Returns the display name of the player
     */
    fun getDisplayName(): String

    /**
     * Returns a new [OfflineCloudPlayer] with the data of this player
     */
    fun toOfflinePlayer(): OfflineCloudPlayer

    /**
     * Returns the web config used for handling web logins
     */
    fun getWebConfig(): PlayerWebConfig

    /**
     * Returns the configuration of this player
     */
    fun toConfiguration(): OfflineCloudPlayerConfiguration

    /**
     * Creates a request to update this player
     */
    fun createUpdateRequest(): OfflineCloudPlayerUpdateRequest

}