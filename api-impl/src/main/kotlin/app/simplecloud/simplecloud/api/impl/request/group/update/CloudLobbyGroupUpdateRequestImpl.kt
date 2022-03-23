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

package app.simplecloud.simplecloud.api.impl.request.group.update

import app.simplecloud.simplecloud.api.image.Image
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessGroupService
import app.simplecloud.simplecloud.api.process.group.CloudLobbyGroup
import app.simplecloud.simplecloud.api.process.group.configuration.CloudLobbyProcessGroupConfiguration
import app.simplecloud.simplecloud.api.process.onlineonfiguration.ProcessesOnlineCountConfiguration
import app.simplecloud.simplecloud.api.request.group.update.CloudLobbyGroupUpdateRequest
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 06.04.2021
 * Time: 10:03
 * @author Frederick Baier
 */
class CloudLobbyGroupUpdateRequestImpl(
    private val internalService: InternalCloudProcessGroupService,
    private val lobbyGroup: CloudLobbyGroup
) : AbstractCloudProcessGroupUpdateRequest(lobbyGroup),
    CloudLobbyGroupUpdateRequest {

    @Volatile
    private var lobbyPriority = this.lobbyGroup.getLobbyPriority()

    override fun setLobbyPriority(lobbyPriority: Int): CloudLobbyGroupUpdateRequest {
        this.lobbyPriority = lobbyPriority
        return this
    }

    override fun getProcessGroup(): CloudLobbyGroup {
        return this.lobbyGroup
    }

    override fun setMaxMemory(memory: Int): CloudLobbyGroupUpdateRequest {
        super.setMaxMemory(memory)
        return this
    }

    override fun setMaxPlayers(players: Int): CloudLobbyGroupUpdateRequest {
        super.setMaxPlayers(players)
        return this
    }

    override fun setImage(image: Image?): CloudLobbyGroupUpdateRequest {
        super.setImage(image)
        return this
    }

    override fun setOnlineCountConfiguration(onlineCountConfiguration: ProcessesOnlineCountConfiguration): CloudLobbyGroupUpdateRequest {
        super.setOnlineCountConfiguration(onlineCountConfiguration)
        return this
    }

    override fun setOnlineCountConfiguration(onlineCountConfigurationFuture: CompletableFuture<ProcessesOnlineCountConfiguration>): CloudLobbyGroupUpdateRequest {
        super.setOnlineCountConfiguration(onlineCountConfigurationFuture)
        return this
    }

    override fun setMaintenance(maintenance: Boolean): CloudLobbyGroupUpdateRequest {
        super.setMaintenance(maintenance)
        return this
    }

    override fun setJoinPermission(permission: String?): CloudLobbyGroupUpdateRequest {
        super.setJoinPermission(permission)
        return this
    }

    override fun setStateUpdating(stateUpdating: Boolean): CloudLobbyGroupUpdateRequest {
        super.setStateUpdating(stateUpdating)
        return this
    }

    override fun setStartPriority(priority: Int): CloudLobbyGroupUpdateRequest {
        super.setStartPriority(priority)
        return this
    }

    override suspend fun submit0(image: Image?, onlineCountConfiguration: ProcessesOnlineCountConfiguration) {
        val updateObj = CloudLobbyProcessGroupConfiguration(
            this.lobbyGroup.getName(),
            this.maxMemory,
            this.maxPlayers,
            this.maintenance,
            image?.getName(),
            onlineCountConfiguration.getName(),
            this.lobbyGroup.isStatic(),
            this.stateUpdating,
            this.startPriority,
            this.joinPermission,
            this.lobbyPriority
        )
        return this.internalService.updateGroupInternal(updateObj)
    }
}