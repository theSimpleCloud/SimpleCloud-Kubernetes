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

package app.simplecloud.simplecloud.api.impl.process.group

import app.simplecloud.simplecloud.api.image.Image
import app.simplecloud.simplecloud.api.impl.image.ImageImpl
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessGroupService
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import app.simplecloud.simplecloud.api.request.group.CloudProcessGroupDeleteRequest
import app.simplecloud.simplecloud.api.service.CloudProcessService
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 27.03.2021
 * Time: 12:01
 * @author Frederick Baier
 *
 * Represents a process group exposed by the api to allow easier accessibility of associated objects
*/
abstract class AbstractCloudProcessGroup constructor(
    private val configuration: AbstractCloudProcessGroupConfiguration,
    private val processService: CloudProcessService,
    private val processGroupService: InternalCloudProcessGroupService,
) : CloudProcessGroup {


    override fun getMaxMemory(): Int {
        return this.configuration.maxMemory
    }

    override fun getMaxPlayers(): Int {
        return this.configuration.maxPlayers
    }

    override fun getOnlinePlayerCount(): Int {
        TODO()
    }

    override fun isInMaintenance(): Boolean {
        return this.configuration.maintenance
    }

    override fun getImage(): Image {
        val imageName = this.configuration.imageName
            ?: throw app.simplecloud.simplecloud.api.impl.exception.NoImageProvidedException(getName())
        return ImageImpl(imageName)
    }

    override fun getJoinPermission(): String? {
        return this.configuration.joinPermission
    }

    override fun isStatic(): Boolean {
        return this.configuration.static
    }

    override fun isStateUpdatingEnabled(): Boolean {
        return this.configuration.stateUpdating
    }

    override fun getStartPriority(): Int {
        return this.configuration.startPriority
    }

    override fun getProcesses(): CompletableFuture<List<CloudProcess>> {
        return this.processService.findByGroup(this)
    }

    override fun getName(): String {
        return this.configuration.name
    }

    override fun getIdentifier(): String {
        return getName()
    }

    override fun createDeleteRequest(): CloudProcessGroupDeleteRequest {
        return this.processGroupService.createDeleteRequest(this)
    }

}