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

package eu.thesimplecloud.simplecloud.api.request.group.update

import eu.thesimplecloud.simplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.simplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.simplecloud.api.process.group.ICloudLobbyGroup
import eu.thesimplecloud.simplecloud.api.process.onlineonfiguration.IProcessesOnlineCountConfiguration
import eu.thesimplecloud.simplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.simplecloud.api.template.ITemplate
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 06.04.2021
 * Time: 09:59
 * @author Frederick Baier
 */
interface ICloudLobbyGroupUpdateRequest : ICloudServerGroupUpdateRequest {

    /**
     * Sets the lobby priority for the group
     * @return this
     */
    fun setLobbyPriority(lobbyPriority: Int): ICloudLobbyGroupUpdateRequest

    override fun getProcessGroup(): ICloudLobbyGroup

    override fun setMaxMemory(memory: Int): ICloudLobbyGroupUpdateRequest

    override fun setMaxPlayers(players: Int): ICloudLobbyGroupUpdateRequest

    override fun setVersion(version: IProcessVersion): ICloudLobbyGroupUpdateRequest

    override fun setVersion(versionFuture: CompletableFuture<IProcessVersion>): ICloudLobbyGroupUpdateRequest

    override fun setTemplate(template: ITemplate): ICloudLobbyGroupUpdateRequest

    override fun setTemplate(templateFuture: CompletableFuture<ITemplate>): ICloudLobbyGroupUpdateRequest

    override fun setJvmArguments(jvmArguments: IJVMArguments?): ICloudLobbyGroupUpdateRequest

    override fun setJvmArguments(jvmArgumentsFuture: CompletableFuture<IJVMArguments>): ICloudLobbyGroupUpdateRequest

    override fun setOnlineCountConfiguration(onlineCountConfiguration: IProcessesOnlineCountConfiguration): ICloudLobbyGroupUpdateRequest

    override fun setOnlineCountConfiguration(onlineCountConfigurationFuture: CompletableFuture<IProcessesOnlineCountConfiguration>): ICloudLobbyGroupUpdateRequest

    override fun setMaintenance(maintenance: Boolean): ICloudLobbyGroupUpdateRequest

    override fun setMinimumOnlineProcessCount(minCount: Int): ICloudLobbyGroupUpdateRequest

    override fun setMaximumOnlineProcessCount(maxCount: Int): ICloudLobbyGroupUpdateRequest

    override fun setJoinPermission(permission: String?): ICloudLobbyGroupUpdateRequest

    override fun setStateUpdating(stateUpdating: Boolean): ICloudLobbyGroupUpdateRequest

    override fun setStartPriority(priority: Int): ICloudLobbyGroupUpdateRequest

    override fun setNodesAllowedToStartOn(node: List<String>): ICloudLobbyGroupUpdateRequest

    override fun submit(): CompletableFuture<ICloudProcessGroup>
}