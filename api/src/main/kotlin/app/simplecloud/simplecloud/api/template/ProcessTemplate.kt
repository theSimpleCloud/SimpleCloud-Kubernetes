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

package app.simplecloud.simplecloud.api.template

import app.simplecloud.simplecloud.api.image.Image
import app.simplecloud.simplecloud.api.process.state.ProcessState
import app.simplecloud.simplecloud.api.request.template.ProcessTemplateDeleteRequest
import app.simplecloud.simplecloud.api.request.template.ProcessTemplateUpdateRequest
import app.simplecloud.simplecloud.api.template.configuration.AbstractProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.utils.Identifiable
import app.simplecloud.simplecloud.api.utils.Nameable

/**
 * Date: 16.08.22
 * Time: 11:15
 * @author Frederick Baier
 *
 * The base interface for static servers and groups
 */
interface ProcessTemplate : Nameable, Identifiable<String> {

    /**
     * Returns the maximum amount of memory process created from this template will have available
     */
    fun getMaxMemory(): Int

    /**
     * Returns the maximum amount of players for processes created from this template
     */
    fun getMaxPlayers(): Int

    /**
     * Returns whether processes created by this template are in maintenance mode
     */
    fun isInMaintenance(): Boolean

    /**
     * Returns the images processes created by this template will get started with
     */
    fun getImage(): Image

    /**
     * Returns the type processes created by this template will be treated as
     */
    fun getProcessTemplateType(): ProcessTemplateType

    /**
     * Returns the permission players will need to join processes created by this template
     */
    fun getJoinPermission(): String?

    /**
     * Returns whether the state of the processes started by this template shall be automatically
     *  changed to [ProcessState.ONLINE]
     */
    fun isStateUpdatingEnabled(): Boolean

    /**
     * Returns whether this template is static
     */
    fun isStatic(): Boolean

    /**
     * Returns the start priority of this template
     */
    fun getStartPriority(): Int

    /**
     * Returns whether this template is active. Templates with active=false will not start.
     */
    fun isActive(): Boolean

    fun toConfiguration(): AbstractProcessTemplateConfiguration

    fun createUpdateRequest(): ProcessTemplateUpdateRequest

    fun createDeleteRequest(): ProcessTemplateDeleteRequest

    override fun getIdentifier(): String {
        return getName()
    }

}