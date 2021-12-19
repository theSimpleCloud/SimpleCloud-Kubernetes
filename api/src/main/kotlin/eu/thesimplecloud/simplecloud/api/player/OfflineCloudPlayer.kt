/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.simplecloud.api.player

import eu.thesimplecloud.simplecloud.api.player.update.OfflinePlayerUpdateRequest
import eu.thesimplecloud.simplecloud.api.utils.Nameable
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * Date: 21.03.2021
 * Time: 15:32
 * @author Frederick Baier
 */
interface OfflineCloudPlayer : Nameable {

    /**
     * Returns the unique id of the player
     */
    fun getUniqueId(): UUID

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
     * Creates a request to update this player
     */
    fun createUpdateRequest(): OfflinePlayerUpdateRequest

}