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

package app.simplecloud.simplecloud.restserver.defaultcontroller.v1.handler

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.impl.image.ImageImpl
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.restserver.defaultcontroller.v1.dto.CloudProcessCreateRequestDto
import com.google.inject.Inject
import com.google.inject.Singleton

/**
 * Created by IntelliJ IDEA.
 * Date: 07/07/2021
 * Time: 20:46
 * @author Frederick Baier
 */
@Singleton
class ProcessCreateHandler @Inject constructor(
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
        val request = this.processService.createProcessStartRequest(group)
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