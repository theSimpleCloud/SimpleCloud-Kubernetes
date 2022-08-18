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

package app.simplecloud.simplecloud.api.request.template

import app.simplecloud.simplecloud.api.image.Image
import app.simplecloud.simplecloud.api.process.state.ProcessState
import app.simplecloud.simplecloud.api.template.ProcessTemplate
import app.simplecloud.simplecloud.api.utils.Request

/**
 * Date: 16.08.22
 * Time: 16:39
 * @author Frederick Baier
 *
 */
interface ProcessTemplateUpdateRequest : Request<Unit> {

    /**
     * Returns the [ProcessTemplate] this request updates
     */
    fun getProcessTemplate(): ProcessTemplate

    /**
     * Sets the maximum amount of memory
     * @return this
     */
    fun setMaxMemory(memory: Int): ProcessTemplateUpdateRequest

    /**
     * Sets the maximum amount of players
     * @return this
     */
    fun setMaxPlayers(players: Int): ProcessTemplateUpdateRequest

    /**
     * Sets the image for the group
     * @return this
     */
    fun setImage(image: Image?): ProcessTemplateUpdateRequest

    /**
     * Sets the maintenance state for the group
     * @return this
     */
    fun setMaintenance(maintenance: Boolean): ProcessTemplateUpdateRequest

    /**
     * Sets the permission a player need to join processes of the group
     * @return this
     */
    fun setJoinPermission(permission: String?): ProcessTemplateUpdateRequest

    /**
     * Sets whether the state of processes shall be automatically set to [ProcessState.ONLINE]
     *  after the process has been started
     * @return this
     */
    fun setStateUpdating(stateUpdating: Boolean): ProcessTemplateUpdateRequest

    /**
     * Sets start priority for the group (higher will be started first)
     * @return this
     */
    fun setStartPriority(priority: Int): ProcessTemplateUpdateRequest

}