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

package app.simplecloud.simplecloud.api.impl.request.template.update

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.api.image.Image
import app.simplecloud.simplecloud.api.request.template.ProcessTemplateUpdateRequest
import app.simplecloud.simplecloud.api.template.ProcessTemplate
import java.util.concurrent.CompletableFuture

/**
 * Date: 17.08.22
 * Time: 15:00
 * @author Frederick Baier
 *
 */
abstract class AbstractProcessTemplateUpdateRequest(
    private val processTemplate: ProcessTemplate,
) : ProcessTemplateUpdateRequest {


    @Volatile
    protected var maxMemory: Int = this.processTemplate.getMaxMemory()

    @Volatile
    protected var maxPlayers: Int = this.processTemplate.getMaxPlayers()

    @Volatile
    protected var maintenance: Boolean = this.processTemplate.isInMaintenance()

    @Volatile
    protected var joinPermission: String? = this.processTemplate.getJoinPermission()

    @Volatile
    protected var stateUpdating: Boolean = this.processTemplate.isStateUpdatingEnabled()

    @Volatile
    protected var startPriority: Int = this.processTemplate.getStartPriority()

    @Volatile
    protected var active: Boolean = this.processTemplate.isActive()

    @Volatile
    protected var image: Image? = runCatching { this.processTemplate.getImage() }.getOrNull()

    override fun getProcessTemplate(): ProcessTemplate {
        return this.processTemplate
    }

    override fun setMaxMemory(memory: Int): ProcessTemplateUpdateRequest {
        require(memory >= 256) { "Memory cannot be lower than 256" }
        this.maxMemory = memory
        return this
    }

    override fun setMaxPlayers(players: Int): ProcessTemplateUpdateRequest {
        require(players >= -1) { "Max Players must be greater than -2" }
        this.maxPlayers = players
        return this
    }

    override fun setImage(image: Image?): ProcessTemplateUpdateRequest {
        this.image = image
        return this
    }

    override fun setMaintenance(maintenance: Boolean): ProcessTemplateUpdateRequest {
        this.maintenance = maintenance
        return this
    }

    override fun setJoinPermission(permission: String?): ProcessTemplateUpdateRequest {
        this.joinPermission = permission
        return this
    }

    override fun setStateUpdating(stateUpdating: Boolean): ProcessTemplateUpdateRequest {
        this.stateUpdating = stateUpdating
        return this
    }

    override fun setStartPriority(priority: Int): ProcessTemplateUpdateRequest {
        this.startPriority = priority
        return this
    }

    override fun setActive(active: Boolean): ProcessTemplateUpdateRequest {
        this.active = active
        return this
    }

    override fun submit(): CompletableFuture<Unit> = CloudScope.future {
        submit0(image)
    }

    abstract suspend fun submit0(image: Image?)

}