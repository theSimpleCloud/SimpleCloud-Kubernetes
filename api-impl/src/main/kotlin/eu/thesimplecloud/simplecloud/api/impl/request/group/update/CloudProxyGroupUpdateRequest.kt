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

import eu.thesimplecloud.simplecloud.api.internal.service.IInternalCloudProcessGroupService
import eu.thesimplecloud.simplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.simplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.simplecloud.api.process.group.configuration.CloudProxyProcessGroupConfiguration
import eu.thesimplecloud.simplecloud.api.process.group.ICloudProxyGroup
import eu.thesimplecloud.simplecloud.api.request.group.update.ICloudProxyGroupUpdateRequest
import eu.thesimplecloud.simplecloud.api.process.onlineonfiguration.IProcessesOnlineCountConfiguration
import eu.thesimplecloud.simplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.simplecloud.api.template.ITemplate
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 06.04.2021
 * Time: 10:03
 * @author Frederick Baier
 */
class CloudProxyGroupUpdateRequest(
    private val internalService: IInternalCloudProcessGroupService,
    private val proxyGroup: ICloudProxyGroup
) : AbstractCloudProcessGroupUpdateRequest(proxyGroup),
    ICloudProxyGroupUpdateRequest {

    @Volatile
    private var startPort = this.proxyGroup.getStartPort()

    override fun setStartPort(startPort: Int): ICloudProxyGroupUpdateRequest {
        this.startPort = startPort
        return this
    }

    override fun getProcessGroup(): ICloudProxyGroup {
        return this.proxyGroup
    }

    override fun setMaxMemory(memory: Int): ICloudProxyGroupUpdateRequest {
        super.setMaxMemory(memory)
        return this
    }

    override fun setMaxPlayers(players: Int): ICloudProxyGroupUpdateRequest {
        super.setMaxPlayers(players)
        return this
    }

    override fun setVersion(version: IProcessVersion): ICloudProxyGroupUpdateRequest {
        super.setVersion(version)
        return this
    }

    override fun setVersion(versionFuture: CompletableFuture<IProcessVersion>): ICloudProxyGroupUpdateRequest {
        super.setVersion(versionFuture)
        return this
    }

    override fun setTemplate(template: ITemplate): ICloudProxyGroupUpdateRequest {
        super.setTemplate(template)
        return this
    }

    override fun setTemplate(templateFuture: CompletableFuture<ITemplate>): ICloudProxyGroupUpdateRequest {
        super.setTemplate(templateFuture)
        return this
    }

    override fun setJvmArguments(jvmArguments: IJVMArguments?): ICloudProxyGroupUpdateRequest {
        super.setJvmArguments(jvmArguments)
        return this
    }

    override fun setJvmArguments(jvmArgumentsFuture: CompletableFuture<IJVMArguments>): ICloudProxyGroupUpdateRequest {
        super.setJvmArguments(jvmArgumentsFuture)
        return this
    }

    override fun setOnlineCountConfiguration(onlineCountConfiguration: IProcessesOnlineCountConfiguration): ICloudProxyGroupUpdateRequest {
        super.setOnlineCountConfiguration(onlineCountConfiguration)
        return this
    }

    override fun setOnlineCountConfiguration(onlineCountConfigurationFuture: CompletableFuture<IProcessesOnlineCountConfiguration>): ICloudProxyGroupUpdateRequest {
        super.setOnlineCountConfiguration(onlineCountConfigurationFuture)
        return this
    }

    override fun setMaintenance(maintenance: Boolean): ICloudProxyGroupUpdateRequest {
        super.setMaintenance(maintenance)
        return this
    }

    override fun setMinimumOnlineProcessCount(minCount: Int): ICloudProxyGroupUpdateRequest {
        super.setMinimumOnlineProcessCount(minCount)
        return this
    }

    override fun setMaximumOnlineProcessCount(maxCount: Int): ICloudProxyGroupUpdateRequest {
        super.setMaximumOnlineProcessCount(maxCount)
        return this
    }

    override fun setJoinPermission(permission: String?): ICloudProxyGroupUpdateRequest {
        super.setJoinPermission(permission)
        return this
    }

    override fun setStateUpdating(stateUpdating: Boolean): ICloudProxyGroupUpdateRequest {
        super.setStateUpdating(stateUpdating)
        return this
    }

    override fun setStartPriority(priority: Int): ICloudProxyGroupUpdateRequest {
        super.setStartPriority(priority)
        return this
    }

    override fun setNodesAllowedToStartOn(nodes: List<String>): ICloudProxyGroupUpdateRequest {
        super.setNodesAllowedToStartOn(nodes)
        return this
    }

    override fun submit0(
        version: IProcessVersion,
        template: ITemplate,
        jvmArguments: IJVMArguments?,
        onlineCountConfiguration: IProcessesOnlineCountConfiguration,
        nodesAllowedToStartOn: List<String>
    ): CompletableFuture<ICloudProcessGroup> {
        val updateObj = CloudProxyProcessGroupConfiguration(
            this.proxyGroup.getName(),
            this.maxMemory,
            this.maxPlayers,
            this.maintenance,
            this.minProcessCount,
            this.maxProcessCount,
            template.getName(),
            jvmArguments?.getName(),
            version.getName(),
            onlineCountConfiguration.getName(),
            this.proxyGroup.isStatic(),
            this.stateUpdating,
            this.startPriority,
            this.joinPermission,
            this.nodesAllowedToStartOn,
            this.startPort
        )
        return this.internalService.updateGroupInternal(updateObj)
    }
}