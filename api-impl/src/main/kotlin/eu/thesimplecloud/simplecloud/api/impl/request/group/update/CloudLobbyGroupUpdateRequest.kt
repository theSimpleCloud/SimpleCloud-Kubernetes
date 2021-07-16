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

import eu.thesimplecloud.simplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.simplecloud.api.internal.service.IInternalCloudProcessGroupService
import eu.thesimplecloud.simplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.simplecloud.api.process.group.configuration.CloudLobbyProcessGroupConfiguration
import eu.thesimplecloud.simplecloud.api.process.group.ICloudLobbyGroup
import eu.thesimplecloud.simplecloud.api.request.group.update.ICloudLobbyGroupUpdateRequest
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
class CloudLobbyGroupUpdateRequest(
    private val internalService: IInternalCloudProcessGroupService,
    private val lobbyGroup: ICloudLobbyGroup
) : AbstractCloudProcessGroupUpdateRequest(lobbyGroup),
    ICloudLobbyGroupUpdateRequest {

    @Volatile
    private var lobbyPriority = this.lobbyGroup.getLobbyPriority()

    override fun setLobbyPriority(lobbyPriority: Int): ICloudLobbyGroupUpdateRequest {
        this.lobbyPriority = lobbyPriority
        return this
    }

    override fun getProcessGroup(): ICloudLobbyGroup {
        return this.lobbyGroup
    }

    override fun setMaxMemory(memory: Int): ICloudLobbyGroupUpdateRequest {
        super.setMaxMemory(memory)
        return this
    }

    override fun setMaxPlayers(players: Int): ICloudLobbyGroupUpdateRequest {
        super.setMaxPlayers(players)
        return this
    }

    override fun setVersion(version: IProcessVersion): ICloudLobbyGroupUpdateRequest {
        super.setVersion(version)
        return this
    }

    override fun setVersion(versionFuture: CompletableFuture<IProcessVersion>): ICloudLobbyGroupUpdateRequest {
        super.setVersion(versionFuture)
        return this
    }

    override fun setTemplate(template: ITemplate): ICloudLobbyGroupUpdateRequest {
        super.setTemplate(template)
        return this
    }

    override fun setTemplate(templateFuture: CompletableFuture<ITemplate>): ICloudLobbyGroupUpdateRequest {
        super.setTemplate(templateFuture)
        return this
    }

    override fun setJvmArguments(jvmArguments: IJVMArguments?): ICloudLobbyGroupUpdateRequest {
        super.setJvmArguments(jvmArguments)
        return this
    }

    override fun setJvmArguments(jvmArgumentsFuture: CompletableFuture<IJVMArguments>): ICloudLobbyGroupUpdateRequest {
        super.setJvmArguments(jvmArgumentsFuture)
        return this
    }

    override fun setOnlineCountConfiguration(onlineCountConfiguration: IProcessesOnlineCountConfiguration): ICloudLobbyGroupUpdateRequest {
        super.setOnlineCountConfiguration(onlineCountConfiguration)
        return this
    }

    override fun setOnlineCountConfiguration(onlineCountConfigurationFuture: CompletableFuture<IProcessesOnlineCountConfiguration>): ICloudLobbyGroupUpdateRequest {
        super.setOnlineCountConfiguration(onlineCountConfigurationFuture)
        return this
    }

    override fun setMaintenance(maintenance: Boolean): ICloudLobbyGroupUpdateRequest {
        super.setMaintenance(maintenance)
        return this
    }

    override fun setMinimumOnlineProcessCount(minCount: Int): ICloudLobbyGroupUpdateRequest {
        super.setMinimumOnlineProcessCount(minCount)
        return this
    }

    override fun setMaximumOnlineProcessCount(maxCount: Int): ICloudLobbyGroupUpdateRequest {
        super.setMaximumOnlineProcessCount(maxCount)
        return this
    }

    override fun setJoinPermission(permission: String?): ICloudLobbyGroupUpdateRequest {
        super.setJoinPermission(permission)
        return this
    }

    override fun setStateUpdating(stateUpdating: Boolean): ICloudLobbyGroupUpdateRequest {
        super.setStateUpdating(stateUpdating)
        return this
    }

    override fun setStartPriority(priority: Int): ICloudLobbyGroupUpdateRequest {
        super.setStartPriority(priority)
        return this
    }

    override fun setNodesAllowedToStartOn(nodes: List<String>): ICloudLobbyGroupUpdateRequest {
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
        val updateObj = CloudLobbyProcessGroupConfiguration(
            this.lobbyGroup.getName(),
            this.maxMemory,
            this.maxPlayers,
            this.maintenance,
            this.minProcessCount,
            this.maxProcessCount,
            template.getName(),
            jvmArguments?.getName(),
            version.getName(),
            onlineCountConfiguration.getName(),
            this.lobbyGroup.isStatic(),
            this.stateUpdating,
            this.startPriority,
            this.joinPermission,
            this.nodesAllowedToStartOn,
            this.lobbyPriority
        )
        return this.internalService.updateGroupInternal(updateObj)
    }
}