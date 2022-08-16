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

import app.simplecloud.simplecloud.api.image.Image
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessGroupService
import app.simplecloud.simplecloud.api.process.group.CloudProxyGroup
import app.simplecloud.simplecloud.api.process.group.configuration.CloudProxyProcessGroupConfiguration
import app.simplecloud.simplecloud.api.process.template.ProcessTemplate
import app.simplecloud.simplecloud.api.request.group.update.CloudProxyGroupUpdateRequest

/**
 * Created by IntelliJ IDEA.
 * Date: 06.04.2021
 * Time: 10:03
 * @author Frederick Baier
 */
class CloudProxyGroupUpdateRequestImpl(
    private val internalService: InternalCloudProcessGroupService,
    private val proxyGroup: CloudProxyGroup
) : AbstractCloudProcessGroupUpdateRequest(proxyGroup),
    CloudProxyGroupUpdateRequest {

    @Volatile
    private var startPort = this.proxyGroup.getStartPort()

    override fun setStartPort(startPort: Int): CloudProxyGroupUpdateRequest {
        this.startPort = startPort
        return this
    }

    override fun getProcessGroup(): CloudProxyGroup {
        return this.proxyGroup
    }

    override fun getProcessTemplate(): ProcessTemplate {
        return this.proxyGroup
    }

    override fun setMaxMemory(memory: Int): CloudProxyGroupUpdateRequest {
        super.setMaxMemory(memory)
        return this
    }

    override fun setMaxPlayers(players: Int): CloudProxyGroupUpdateRequest {
        super.setMaxPlayers(players)
        return this
    }

    override fun setImage(image: Image?): CloudProxyGroupUpdateRequest {
        super.setImage(image)
        return this
    }

    override fun setMaintenance(maintenance: Boolean): CloudProxyGroupUpdateRequest {
        super.setMaintenance(maintenance)
        return this
    }

    override fun setJoinPermission(permission: String?): CloudProxyGroupUpdateRequest {
        super.setJoinPermission(permission)
        return this
    }

    override fun setStateUpdating(stateUpdating: Boolean): CloudProxyGroupUpdateRequest {
        super.setStateUpdating(stateUpdating)
        return this
    }

    override fun setStartPriority(priority: Int): CloudProxyGroupUpdateRequest {
        super.setStartPriority(priority)
        return this
    }

    override suspend fun submit0(image: Image?) {
        val updateObj = CloudProxyProcessGroupConfiguration(
            this.proxyGroup.getName(),
            this.maxMemory,
            this.maxPlayers,
            this.maintenance,
            image?.getName(),
            this.stateUpdating,
            this.startPriority,
            this.joinPermission,
            this.startPort
        )
        return this.internalService.updateGroupInternal(updateObj)
    }
}