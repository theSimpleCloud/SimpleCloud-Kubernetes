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

package eu.thesimplecloud.simplecloud.api.impl.request.group.update

import eu.thesimplecloud.simplecloud.api.image.Image
import eu.thesimplecloud.simplecloud.api.jvmargs.JVMArguments
import eu.thesimplecloud.simplecloud.api.internal.service.InternalCloudProcessGroupService
import eu.thesimplecloud.simplecloud.api.process.group.CloudProcessGroup
import eu.thesimplecloud.simplecloud.api.process.group.configuration.CloudServerProcessGroupConfiguration
import eu.thesimplecloud.simplecloud.api.process.group.CloudServerGroup
import eu.thesimplecloud.simplecloud.api.request.group.update.CloudServerGroupUpdateRequest
import eu.thesimplecloud.simplecloud.api.process.onlineonfiguration.ProcessesOnlineCountConfiguration
import eu.thesimplecloud.simplecloud.api.process.version.ProcessVersion
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 05.04.2021
 * Time: 22:03
 * @author Frederick Baier
 */
class CloudServerGroupUpdateRequestImpl(
    private val internalService: InternalCloudProcessGroupService,
    private val serverGroup: CloudServerGroup
) : AbstractCloudProcessGroupUpdateRequest(serverGroup),
    CloudServerGroupUpdateRequest {

    override fun submit0(
        version: ProcessVersion,
        image: Image,
        jvmArguments: JVMArguments?,
        onlineCountConfiguration: ProcessesOnlineCountConfiguration
    ): CompletableFuture<CloudProcessGroup> {
        val updateObj = CloudServerProcessGroupConfiguration(
            this.serverGroup.getName(),
            this.maxMemory,
            this.maxPlayers,
            this.maintenance,
            this.minProcessCount,
            this.maxProcessCount,
            image.getName(),
            jvmArguments?.getName(),
            version.getName(),
            onlineCountConfiguration.getName(),
            this.serverGroup.isStatic(),
            this.stateUpdating,
            this.startPriority,
            this.joinPermission
        )
        return this.internalService.updateGroupInternal(updateObj)
    }
}