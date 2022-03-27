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

package app.simplecloud.simplecloud.api.process

import app.simplecloud.simplecloud.api.image.Image
import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.process.group.ProcessGroupType
import app.simplecloud.simplecloud.api.process.state.ProcessState
import app.simplecloud.simplecloud.api.request.process.ProcessShutdownRequest
import app.simplecloud.simplecloud.api.request.process.ProcessUpdateRequest
import app.simplecloud.simplecloud.api.utils.Address
import app.simplecloud.simplecloud.api.utils.Identifiable
import app.simplecloud.simplecloud.api.utils.NetworkComponent
import java.util.*
import java.util.concurrent.CompletableFuture

interface CloudProcess : NetworkComponent, Identifiable<String> {

    /**
     * Returns the unique id of the process
     */
    fun getUniqueId(): UUID

    /**
     * Returns the group name of this process
     */
    fun getGroupName(): String

    /**
     * Returns the group of the process
     */
    fun getGroup(): CompletableFuture<CloudProcessGroup>

    /**
     * Returns the process number
     * e.g The name is Lobby-2 -> 2 would be the process number
     */
    fun getProcessNumber(): Int

    /**
     * Returns the state of the process
     */
    fun getState(): ProcessState

    /**
     * Returns whether this process is visible. This decides for example whether the process is shown on signs.
     */
    fun isVisible(): Boolean

    /**
     * Returns the maximum amount of memory this process can use in MB
     */
    fun getMaxMemory(): Int

    /**
     * Returns the amount of memory the process currently uses in MB
     */
    fun getUsedMemory(): Int

    /**
     * Returns the amount of players currently connected to this process
     */
    fun getOnlinePlayers(): Int

    /**
     * Returns the maximum amount of players that can be simultaneously connected the process
     */
    fun getMaxPlayers(): Int

    /**
     * Returns the address of the process
     */
    fun getAddress(): Address

    /**
     * Returns whether the process is static
     */
    fun isStatic(): Boolean

    /**
     * Returns the process type
     */
    fun getProcessType(): ProcessGroupType

    /**
     * Returns the image this process was started from
     */
    fun getImage(): Image

    /**
     * Returns the termination future
     * The termination future will be completed when the process was stopped
     * This methods always returns the same [CompletableFuture]
     */
    fun terminationFuture(): CompletableFuture<Void>

    /**
     * Returns the started future
     * The started future will be completed when the process was started
     * This methods always returns the same [CompletableFuture]
     */
    fun startedFuture(): CompletableFuture<Void>

    /**
     * Returns the configuration of this group
     */
    fun toConfiguration(): CloudProcessConfiguration

    /**
     * Creates a request to update this service
     */
    fun createUpdateRequest(): ProcessUpdateRequest

    /**
     * Creates a request to stop this service
     */
    fun createShutdownRequest(): ProcessShutdownRequest

}