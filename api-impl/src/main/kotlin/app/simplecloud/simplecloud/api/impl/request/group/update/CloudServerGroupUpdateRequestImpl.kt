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
import app.simplecloud.simplecloud.api.process.group.CloudServerGroup
import app.simplecloud.simplecloud.api.process.group.configuration.CloudServerProcessGroupConfiguration
import app.simplecloud.simplecloud.api.process.onlineonfiguration.ProcessesOnlineCountConfiguration
import app.simplecloud.simplecloud.api.request.group.update.CloudServerGroupUpdateRequest
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
        image: Image?,
        onlineCountConfiguration: ProcessesOnlineCountConfiguration
    ): CompletableFuture<Unit> {
        val updateObj = CloudServerProcessGroupConfiguration(
            this.serverGroup.getName(),
            this.maxMemory,
            this.maxPlayers,
            this.maintenance,
            image?.getName(),
            onlineCountConfiguration.getName(),
            this.serverGroup.isStatic(),
            this.stateUpdating,
            this.startPriority,
            this.joinPermission
        )
        return this.internalService.updateGroupInternal(updateObj)
    }
}