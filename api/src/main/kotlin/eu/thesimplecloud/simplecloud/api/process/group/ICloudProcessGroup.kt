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

package eu.thesimplecloud.simplecloud.api.process.group

import eu.thesimplecloud.simplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.simplecloud.api.node.INode
import eu.thesimplecloud.simplecloud.api.process.ICloudProcess
import eu.thesimplecloud.simplecloud.api.process.ProcessGroupType
import eu.thesimplecloud.simplecloud.api.process.group.update.ICloudProcessGroupUpdateRequest
import eu.thesimplecloud.simplecloud.api.process.onlineonfiguration.IProcessesOnlineCountConfiguration
import eu.thesimplecloud.simplecloud.api.process.request.IProcessGroupDeleteRequest
import eu.thesimplecloud.simplecloud.api.process.request.IProcessStartRequest
import eu.thesimplecloud.simplecloud.api.template.ITemplate
import eu.thesimplecloud.simplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.simplecloud.api.repository.IIdentifiable
import eu.thesimplecloud.simplecloud.api.utils.INameable
import java.util.concurrent.CompletableFuture

interface ICloudProcessGroup : INameable, IIdentifiable<String> {

    fun getMaxMemory(): Int

    fun getMaxPlayers(): Int

    fun getOnlinePlayerCount(): Int

    fun isInMaintenance(): Boolean

    fun getTemplate(): CompletableFuture<ITemplate>

    fun getVersion(): CompletableFuture<IProcessVersion>

    fun getJvmArguments(): CompletableFuture<IJVMArguments>

    fun getProcessGroupType(): ProcessGroupType

    fun getJoinPermission(): String?

    fun getMinimumOnlineProcessCount(): Int

    fun getMaximumOnlineProcessCount(): Int

    fun isStatic(): Boolean

    fun isStateUpdatingEnabled(): Boolean

    fun getStartPriority(): Int

    fun getNodesAllowedToStartServicesOn(): CompletableFuture<List<INode>>

    fun getProcesses(): CompletableFuture<List<ICloudProcess>>

    fun getProcessOnlineCountConfiguration(): CompletableFuture<IProcessesOnlineCountConfiguration>

    fun createProcessStartRequest(): IProcessStartRequest

    fun createUpdateRequest(): ICloudProcessGroupUpdateRequest

    fun createDeleteRequest(): IProcessGroupDeleteRequest

}