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

import eu.thesimplecloud.simplecloud.api.CloudAPI
import eu.thesimplecloud.simplecloud.api.impl.future.exception.CompletedWithNullException
import eu.thesimplecloud.simplecloud.api.impl.process.request.ProcessStopRequest
import eu.thesimplecloud.simplecloud.api.impl.utils.AbstractNetworkComponent
import eu.thesimplecloud.simplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.simplecloud.api.node.INode
import eu.thesimplecloud.simplecloud.api.process.ICloudProcess
import eu.thesimplecloud.simplecloud.api.process.ProcessGroupType
import eu.thesimplecloud.simplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.simplecloud.api.process.request.IProcessStopRequest
import eu.thesimplecloud.simplecloud.api.process.state.ProcessState
import eu.thesimplecloud.simplecloud.api.template.ITemplate
import eu.thesimplecloud.simplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.simplecloud.api.utils.Address
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 27.03.2021
 * Time: 09:14
 * @author Frederick Baier
 */
class CloudProcess(
    private val groupName: String,
    private val uniqueId: UUID,
    private val processNumber: Int,
    private val state: ProcessState,
    private val maxMemory: Int,
    private val usedMemory: Int,
    private val maxPlayers: Int,
    private val address: Address,
    private val static: Boolean,
    private val processGroupType: ProcessGroupType,
    private val versionName: String,
    private val templateName: String,
    private val nodeNameRunningOn: String,
    private val jvmArgumentsName: String?,
) : AbstractNetworkComponent(), ICloudProcess {

    override fun getGroup(): CompletableFuture<ICloudProcessGroup> {
        return CloudAPI.instance.getProcessGroupService().findByName(this.groupName)
    }

    override fun getProcessNumber(): Int {
        return this.processNumber
    }

    override fun getState(): ProcessState {
        return this.state
    }

    override fun getMaxMemory(): Int {
        return this.maxMemory
    }

    override fun getUsedMemory(): Int {
        return this.usedMemory
    }

    override fun getMaxPlayers(): Int {
        return this.maxPlayers
    }

    override fun getAddress(): Address {
        return this.address
    }

    override fun isStatic(): Boolean {
        return this.static
    }

    override fun getProcessType(): ProcessGroupType {
        return this.processGroupType
    }

    override fun getVersion(): CompletableFuture<IProcessVersion> {
        return CloudAPI.instance.getProcessVersionService().findByName(this.versionName)
    }

    override fun getTemplate(): CompletableFuture<ITemplate> {
        return CloudAPI.instance.getTemplateService().findByName(this.templateName)
    }

    override fun getJvmArguments(): CompletableFuture<IJVMArguments> {
        if (this.jvmArgumentsName == null) return CompletableFuture.failedFuture(CompletedWithNullException())
        return CloudAPI.instance.getJvmArgumentsService().findByName(this.jvmArgumentsName)
    }

    override fun getNodeRunningOn(): CompletableFuture<INode> {
        return CloudAPI.instance.getNodeService().findNodeByName(this.nodeNameRunningOn)
    }

    override fun terminationFuture(): CompletableFuture<Void> {
        TODO("Not yet implemented")
    }

    override fun startedFuture(): CompletableFuture<Void> {
        TODO("Not yet implemented")
    }

    override fun createStopRequest(): IProcessStopRequest {
        return ProcessStopRequest(this)
    }

    override fun getUniqueId(): UUID {
        return this.uniqueId
    }

    override fun getName(): String {
        return this.groupName + "-" + getProcessNumber()
    }

    override fun getIdentifier(): String {
        return getName()
    }


}