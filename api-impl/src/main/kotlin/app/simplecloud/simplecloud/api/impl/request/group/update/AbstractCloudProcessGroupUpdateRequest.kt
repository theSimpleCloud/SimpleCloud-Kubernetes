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

package app.simplecloud.simplecloud.api.impl.request.group.update

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.api.image.Image
import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.request.group.update.CloudProcessGroupUpdateRequest
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 04.04.2021
 * Time: 21:35
 * @author Frederick Baier
 */
abstract class AbstractCloudProcessGroupUpdateRequest(
    private val processGroup: CloudProcessGroup
) : CloudProcessGroupUpdateRequest {

    @Volatile
    protected var maxMemory: Int = this.processGroup.getMaxMemory()

    @Volatile
    protected var maxPlayers: Int = this.processGroup.getMaxPlayers()

    @Volatile
    protected var maintenance: Boolean = this.processGroup.isInMaintenance()

    @Volatile
    protected var joinPermission: String? = this.processGroup.getJoinPermission()

    @Volatile
    protected var stateUpdating: Boolean = this.processGroup.isStateUpdatingEnabled()

    @Volatile
    protected var startPriority: Int = this.processGroup.getStartPriority()

    @Volatile
    protected var image: Image? = runCatching { this.processGroup.getImage() }.getOrNull()

    override fun getProcessGroup(): CloudProcessGroup {
        return this.processGroup
    }

    override fun setMaxMemory(memory: Int): CloudProcessGroupUpdateRequest {
        this.maxMemory = memory
        return this
    }

    override fun setMaxPlayers(players: Int): CloudProcessGroupUpdateRequest {
        this.maxPlayers = players
        return this
    }

    override fun setImage(image: Image?): CloudProcessGroupUpdateRequest {
        this.image = image
        return this
    }

    override fun setMaintenance(maintenance: Boolean): CloudProcessGroupUpdateRequest {
        this.maintenance = maintenance
        return this
    }

    override fun setJoinPermission(permission: String?): CloudProcessGroupUpdateRequest {
        this.joinPermission = permission
        return this
    }

    override fun setStateUpdating(stateUpdating: Boolean): CloudProcessGroupUpdateRequest {
        this.stateUpdating = stateUpdating
        return this
    }

    override fun setStartPriority(priority: Int): CloudProcessGroupUpdateRequest {
        this.startPriority = priority
        return this
    }

    override fun submit(): CompletableFuture<Unit> = CloudScope.future {
        submit0(image)
    }

    abstract suspend fun submit0(image: Image?)


}