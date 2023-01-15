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

package app.simplecloud.simplecloud.node.defaultcontroller.v1.handler

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.image.ImageImpl
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.api.template.group.CloudProcessGroup
import app.simplecloud.simplecloud.node.defaultcontroller.v1.dto.CloudProcessCreateRequestDto

/**
 * Created by IntelliJ IDEA.
 * Date: 07/07/2021
 * Time: 20:46
 * @author Frederick Baier
 */
class ProcessCreateHandler(
    private val groupService: CloudProcessGroupService,
    private val processService: CloudProcessService
) {

    suspend fun create(configuration: CloudProcessCreateRequestDto): CloudProcess {
        val group = this.groupService.findByName(configuration.groupName).await()
        return createProcess(group, configuration)
    }

    private suspend fun createProcess(
        group: CloudProcessGroup,
        configuration: CloudProcessCreateRequestDto
    ): CloudProcess {
        val request = this.processService.createStartRequest(group)
        if (configuration.maxMemory != null) {
            request.setMaxMemory(configuration.maxMemory)
        }
        if (configuration.maxPlayers != null) {
            request.setMaxPlayers(configuration.maxPlayers)
        }
        if (configuration.imageName != null) {
            request.setImage(ImageImpl(configuration.imageName))
        }
        return request.submit().await()
    }

}