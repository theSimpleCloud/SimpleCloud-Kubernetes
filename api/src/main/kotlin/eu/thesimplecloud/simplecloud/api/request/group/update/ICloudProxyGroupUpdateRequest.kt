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
import eu.thesimplecloud.simplecloud.api.process.group.ICloudProxyGroup
import eu.thesimplecloud.simplecloud.api.process.onlineonfiguration.IProcessesOnlineCountConfiguration
import eu.thesimplecloud.simplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.simplecloud.api.template.ITemplate
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 06.04.2021
 * Time: 11:19
 * @author Frederick Baier
 */
interface ICloudProxyGroupUpdateRequest : ICloudProcessGroupUpdateRequest {

    /**
     * Sets the start priority for the group
     * @return this
     */
    fun setStartPort(startPort: Int): ICloudProxyGroupUpdateRequest

    override fun getProcessGroup(): ICloudProxyGroup

    override fun setMaxMemory(memory: Int): ICloudProxyGroupUpdateRequest

    override fun setMaxPlayers(players: Int): ICloudProxyGroupUpdateRequest

    override fun setVersion(version: IProcessVersion): ICloudProxyGroupUpdateRequest

    override fun setVersion(versionFuture: CompletableFuture<IProcessVersion>): ICloudProxyGroupUpdateRequest

    override fun setTemplate(template: ITemplate): ICloudProxyGroupUpdateRequest

    override fun setTemplate(templateFuture: CompletableFuture<ITemplate>): ICloudProxyGroupUpdateRequest

    override fun setJvmArguments(jvmArguments: IJVMArguments?): ICloudProxyGroupUpdateRequest

    override fun setJvmArguments(jvmArgumentsFuture: CompletableFuture<IJVMArguments>): ICloudProxyGroupUpdateRequest

    override fun setOnlineCountConfiguration(onlineCountConfiguration: IProcessesOnlineCountConfiguration): ICloudProxyGroupUpdateRequest

    override fun setOnlineCountConfiguration(onlineCountConfigurationFuture: CompletableFuture<IProcessesOnlineCountConfiguration>): ICloudProxyGroupUpdateRequest

    override fun setMaintenance(maintenance: Boolean): ICloudProxyGroupUpdateRequest

    override fun setMinimumOnlineProcessCount(minCount: Int): ICloudProxyGroupUpdateRequest

    override fun setMaximumOnlineProcessCount(maxCount: Int): ICloudProxyGroupUpdateRequest

    override fun setJoinPermission(permission: String?): ICloudProxyGroupUpdateRequest

    override fun setStateUpdating(stateUpdating: Boolean): ICloudProxyGroupUpdateRequest

    override fun setStartPriority(priority: Int): ICloudProxyGroupUpdateRequest

    override fun setNodesAllowedToStartOn(nodes: List<String>): ICloudProxyGroupUpdateRequest

    override fun submit(): CompletableFuture<ICloudProcessGroup>

}