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

package eu.thesimplecloud.simplecloud.api.impl.process

import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import eu.thesimplecloud.simplecloud.api.future.exception.CompletedWithNullException
import eu.thesimplecloud.simplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.simplecloud.api.node.INode
import eu.thesimplecloud.simplecloud.api.process.CloudProcessConfiguration
import eu.thesimplecloud.simplecloud.api.process.ICloudProcess
import eu.thesimplecloud.simplecloud.api.process.group.ProcessGroupType
import eu.thesimplecloud.simplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.simplecloud.api.process.state.ProcessState
import eu.thesimplecloud.simplecloud.api.template.ITemplate
import eu.thesimplecloud.simplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.simplecloud.api.request.process.IProcessShutdownRequest
import eu.thesimplecloud.simplecloud.api.service.*
import eu.thesimplecloud.simplecloud.api.utils.Address
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 27.03.2021
 * Time: 09:14
 * @author Frederick Baier
 */
class CloudProcess @Inject constructor(
    @Assisted private val configuration: CloudProcessConfiguration,
    private val processService: ICloudProcessService,
    private val processGroupService: ICloudProcessGroupService,
    private val processVersionService: IProcessVersionService,
    private val templateService: ITemplateService,
    private val jvmArgumentService: IJvmArgumentsService,
    private val nodeService: INodeService,
) : ICloudProcess {

    override fun getGroupName(): String {
        return this.configuration.groupName
    }

    override fun getGroup(): CompletableFuture<ICloudProcessGroup> {
        return this.processGroupService.findByName(getGroupName())
    }

    override fun getProcessNumber(): Int {
        return this.configuration.processNumber
    }

    override fun getState(): ProcessState {
        return this.configuration.state
    }

    override fun getMaxMemory(): Int {
        return this.configuration.maxMemory
    }

    override fun getUsedMemory(): Int {
        return this.configuration.usedMemory
    }

    override fun getMaxPlayers(): Int {
        return this.configuration.maxPlayers
    }

    override fun getAddress(): Address {
        return this.configuration.address
    }

    override fun isStatic(): Boolean {
        return this.configuration.static
    }

    override fun getProcessType(): ProcessGroupType {
        return this.configuration.processGroupType
    }

    override fun getVersion(): CompletableFuture<IProcessVersion> {
        return this.processVersionService.findByName(this.configuration.versionName)
    }

    override fun getTemplate(): CompletableFuture<ITemplate> {
        return this.templateService.findByName(this.configuration.templateName)
    }

    override fun getJvmArguments(): CompletableFuture<IJVMArguments> {
        val jvmArgumentsName = this.configuration.jvmArgumentsName
            ?: return CompletableFuture.failedFuture(CompletedWithNullException())
        return this.jvmArgumentService.findByName(jvmArgumentsName)
    }

    override fun getNodeRunningOn(): CompletableFuture<INode> {
        return this.nodeService.findNodeByName(this.configuration.nodeNameRunningOn)
    }

    override fun terminationFuture(): CompletableFuture<Void> {
        TODO("Not yet implemented")
    }

    override fun startedFuture(): CompletableFuture<Void> {
        TODO("Not yet implemented")
    }

    override fun getUniqueId(): UUID {
        return this.configuration.uniqueId
    }

    override fun getName(): String {
        return getGroupName() + "-" + getProcessNumber()
    }

    override fun getIdentifier(): String {
        return getName()
    }

    override fun toConfiguration(): CloudProcessConfiguration {
        return this.configuration
    }

    override fun getIgniteId(): UUID {
        return this.configuration.igniteId ?: throw NullPointerException("Ignite id not set")
    }

    override fun createShutdownRequest(): IProcessShutdownRequest {
        return this.processService.createProcessShutdownRequest(this)
    }

}