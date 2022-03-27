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

package app.simplecloud.simplecloud.api.request.group.update

import app.simplecloud.simplecloud.api.image.Image
import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.process.state.ProcessState
import app.simplecloud.simplecloud.api.utils.Request

/**
 * Created by IntelliJ IDEA.
 * Date: 17.03.2021
 * Time: 18:59
 * @author Frederick Baier
 *
 * Request for updating a registered group
 *
 */
interface CloudProcessGroupUpdateRequest : Request<Unit> {

    /**
     * Returns the group this request updates
     */
    fun getProcessGroup(): CloudProcessGroup

    /**
     * Sets the maximum amount of memory
     * @return this
     */
    fun setMaxMemory(memory: Int): CloudProcessGroupUpdateRequest

    /**
     * Sets the maximum amount of players
     * @return this
     */
    fun setMaxPlayers(players: Int): CloudProcessGroupUpdateRequest

    /**
     * Sets the image for the group
     * @return this
     */
    fun setImage(image: Image?): CloudProcessGroupUpdateRequest

    /**
     * Sets the maintenance state for the group
     * @return this
     */
    fun setMaintenance(maintenance: Boolean): CloudProcessGroupUpdateRequest

    /**
     * Sets the permission a player need to join processes of the group
     * @return this
     */
    fun setJoinPermission(permission: String?): CloudProcessGroupUpdateRequest

    /**
     * Sets whether the state of processes shall be automatically set to [ProcessState.ONLINE]
     *  after the process has been started
     * @return this
     */
    fun setStateUpdating(stateUpdating: Boolean): CloudProcessGroupUpdateRequest

    /**
     * Sets start priority for the group (higher will be started first)
     * @return this
     */
    fun setStartPriority(priority: Int): CloudProcessGroupUpdateRequest



}