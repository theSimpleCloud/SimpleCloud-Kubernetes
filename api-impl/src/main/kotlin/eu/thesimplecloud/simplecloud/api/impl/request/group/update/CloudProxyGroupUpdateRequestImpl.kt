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
import eu.thesimplecloud.simplecloud.api.internal.service.InternalCloudProcessGroupService
import eu.thesimplecloud.simplecloud.api.process.group.CloudProcessGroup
import eu.thesimplecloud.simplecloud.api.process.group.CloudProxyGroup
import eu.thesimplecloud.simplecloud.api.process.group.configuration.CloudProxyProcessGroupConfiguration
import eu.thesimplecloud.simplecloud.api.process.onlineonfiguration.ProcessesOnlineCountConfiguration
import eu.thesimplecloud.simplecloud.api.request.group.update.CloudProxyGroupUpdateRequest
import java.util.concurrent.CompletableFuture

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

    override fun setOnlineCountConfiguration(onlineCountConfiguration: ProcessesOnlineCountConfiguration): CloudProxyGroupUpdateRequest {
        super.setOnlineCountConfiguration(onlineCountConfiguration)
        return this
    }

    override fun setOnlineCountConfiguration(onlineCountConfigurationFuture: CompletableFuture<ProcessesOnlineCountConfiguration>): CloudProxyGroupUpdateRequest {
        super.setOnlineCountConfiguration(onlineCountConfigurationFuture)
        return this
    }

    override fun setMaintenance(maintenance: Boolean): CloudProxyGroupUpdateRequest {
        super.setMaintenance(maintenance)
        return this
    }

    override fun setMinimumOnlineProcessCount(minCount: Int): CloudProxyGroupUpdateRequest {
        super.setMinimumOnlineProcessCount(minCount)
        return this
    }

    override fun setMaximumOnlineProcessCount(maxCount: Int): CloudProxyGroupUpdateRequest {
        super.setMaximumOnlineProcessCount(maxCount)
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

    override fun submit0(
        image: Image?,
        onlineCountConfiguration: ProcessesOnlineCountConfiguration
    ): CompletableFuture<CloudProcessGroup> {
        val updateObj = CloudProxyProcessGroupConfiguration(
            this.proxyGroup.getName(),
            this.maxMemory,
            this.maxPlayers,
            this.maintenance,
            this.minProcessCount,
            this.maxProcessCount,
            image?.getName(),
            onlineCountConfiguration.getName(),
            this.proxyGroup.isStatic(),
            this.stateUpdating,
            this.startPriority,
            this.joinPermission,
            this.startPort
        )
        return this.internalService.updateGroupInternal(updateObj)
    }
}