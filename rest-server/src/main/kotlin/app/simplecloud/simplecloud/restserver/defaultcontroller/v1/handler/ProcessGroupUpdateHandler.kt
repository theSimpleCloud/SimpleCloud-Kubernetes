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

package app.simplecloud.simplecloud.restserver.defaultcontroller.v1.handler

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.impl.image.ImageImpl
import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import app.simplecloud.simplecloud.api.process.group.configuration.CloudLobbyProcessGroupConfiguration
import app.simplecloud.simplecloud.api.process.group.configuration.CloudProxyProcessGroupConfiguration
import app.simplecloud.simplecloud.api.request.group.update.CloudLobbyGroupUpdateRequest
import app.simplecloud.simplecloud.api.request.group.update.CloudProxyGroupUpdateRequest
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.validator.ValidatorService
import com.google.inject.Inject
import com.google.inject.Singleton

/**
 * Created by IntelliJ IDEA.
 * Date: 07/07/2021
 * Time: 20:46
 * @author Frederick Baier
 */
@Singleton
class ProcessGroupUpdateHandler @Inject constructor(
    private val validatorService: ValidatorService,
    private val groupService: CloudProcessGroupService
) {

    suspend fun update(configuration: AbstractCloudProcessGroupConfiguration) {
        val group = this.groupService.findByName(configuration.name).await()
        updateGroup(group, configuration)
    }

    private suspend fun updateGroup(group: CloudProcessGroup, configuration: AbstractCloudProcessGroupConfiguration) {
        this.validatorService.getValidator(configuration::class.java).validate(configuration).await()
        val request = group.createUpdateRequest()
        request.setMaxMemory(configuration.maxMemory)
        request.setMaxPlayers(configuration.maxPlayers)
        request.setImage(ImageImpl.fromName(configuration.imageName))
        request.setMaintenance(configuration.maintenance)
        request.setJoinPermission(configuration.joinPermission)
        request.setStateUpdating(configuration.stateUpdating)
        request.setStartPriority(configuration.startPriority)

        if (request is CloudProxyGroupUpdateRequest) {
            configuration as CloudProxyProcessGroupConfiguration
            request.setStartPort(configuration.startPort)
        }

        if (request is CloudLobbyGroupUpdateRequest) {
            configuration as CloudLobbyProcessGroupConfiguration
            request.setLobbyPriority(configuration.lobbyPriority)
        }

        return request.submit().await()
    }

}