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
import eu.thesimplecloud.simplecloud.api.process.ICloudProcess
import eu.thesimplecloud.simplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.simplecloud.api.service.*
import eu.thesimplecloud.simplecloud.restserver.defaultcontroller.v1.dto.CloudProcessCreateRequestDto

/**
 * Created by IntelliJ IDEA.
 * Date: 07/07/2021
 * Time: 20:46
 * @author Frederick Baier
 */
@Singleton
class ProcessCreateHandler @Inject constructor(
    private val groupService: ICloudProcessGroupService,
    private val processService: ICloudProcessService,
    private val templateService: ITemplateService,
    private val jvmArgumentsService: IJvmArgumentsService,
    private val onlineCountService: IProcessOnlineCountService,
    private val versionService: IProcessVersionService
) {

    fun create(configuration: CloudProcessCreateRequestDto): ICloudProcess {
        val group = await(this.groupService.findByName(configuration.groupName))
        return createProcess(group, configuration)
    }

    private fun createProcess(group: ICloudProcessGroup, configuration: CloudProcessCreateRequestDto): ICloudProcess {
        val request = this.processService.createProcessStartRequest(group)
        if (configuration.maxMemory != null) {
            request.setMaxMemory(configuration.maxMemory)
        }
        if (configuration.maxPlayers != null) {
            request.setMaxPlayers(configuration.maxPlayers)
        }
        if (configuration.versionName != null) {
            request.setProcessVersion(this.versionService.findByName(configuration.versionName))
        }
        if (configuration.templateName != null) {
            request.setTemplate(this.templateService.findByName(configuration.templateName))
        }
        if (configuration.jvmArgumentsName != null) {
            request.setJvmArguments(this.jvmArgumentsService.findByName(configuration.jvmArgumentsName))
        }
        return await(request.submit())
    }

}