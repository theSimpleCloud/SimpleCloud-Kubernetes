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

package eu.thesimplecloud.simplecloud.restserver.defaultcontroller.v1.handler

import com.ea.async.Async.await
import com.google.inject.Inject
import com.google.inject.Singleton
import eu.thesimplecloud.simplecloud.api.impl.image.ImageImpl
import eu.thesimplecloud.simplecloud.api.process.group.CloudProcessGroup
import eu.thesimplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import eu.thesimplecloud.simplecloud.api.process.group.configuration.CloudLobbyProcessGroupConfiguration
import eu.thesimplecloud.simplecloud.api.process.group.configuration.CloudProxyProcessGroupConfiguration
import eu.thesimplecloud.simplecloud.api.request.group.update.CloudLobbyGroupUpdateRequest
import eu.thesimplecloud.simplecloud.api.request.group.update.CloudProxyGroupUpdateRequest
import eu.thesimplecloud.simplecloud.api.service.*
import eu.thesimplecloud.simplecloud.api.validator.ValidatorService

/**
 * Created by IntelliJ IDEA.
 * Date: 07/07/2021
 * Time: 20:46
 * @author Frederick Baier
 */
@Singleton
class ProcessGroupUpdateHandler @Inject constructor(
    private val validatorService: ValidatorService,
    private val groupService: CloudProcessGroupService,
    private val jvmArgumentsService: JvmArgumentsService,
    private val onlineCountService: ProcessOnlineCountService,
    private val versionService: ProcessVersionService
) {

    fun update(configuration: AbstractCloudProcessGroupConfiguration) {
        val group = await(this.groupService.findByName(configuration.name))
        updateGroup(group, configuration)
    }

    private fun updateGroup(group: CloudProcessGroup, configuration: AbstractCloudProcessGroupConfiguration) {
        this.validatorService.getValidator(configuration::class.java).validate(configuration)
        val request = this.groupService.createGroupUpdateRequest(group)
        request.setMaxMemory(configuration.maxMemory)
        request.setMaxPlayers(configuration.maxPlayers)
        request.setVersion(this.versionService.findByName(configuration.versionName))
        request.setImage(ImageImpl(configuration.imageName))
        request.setOnlineCountConfiguration(this.onlineCountService.findByName(configuration.onlineCountConfigurationName))
        request.setMaintenance(configuration.maintenance)
        request.setMinimumOnlineProcessCount(configuration.minimumProcessCount)
        request.setMaximumOnlineProcessCount(configuration.maximumProcessCount)
        request.setJoinPermission(configuration.joinPermission)
        request.setStateUpdating(configuration.stateUpdating)
        request.setStartPriority(configuration.startPriority)

        val jvmArgumentName = configuration.jvmArgumentName
        if (jvmArgumentName == null) {
            request.setJvmArguments(null)
        } else {
            request.setJvmArguments(this.jvmArgumentsService.findByName(jvmArgumentName))
        }

        if (request is CloudProxyGroupUpdateRequest) {
            configuration as CloudProxyProcessGroupConfiguration
            request.setStartPort(configuration.startPort)
        }

        if (request is CloudLobbyGroupUpdateRequest) {
            configuration as CloudLobbyProcessGroupConfiguration
            request.setLobbyPriority(configuration.lobbyPriority)
        }

        request.submit().join()
    }

}