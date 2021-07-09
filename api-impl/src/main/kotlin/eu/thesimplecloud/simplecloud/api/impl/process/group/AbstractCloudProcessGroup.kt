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

package eu.thesimplecloud.simplecloud.api.impl.process.group

import eu.thesimplecloud.simplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.simplecloud.api.node.INode
import eu.thesimplecloud.simplecloud.api.process.ICloudProcess
import eu.thesimplecloud.simplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import eu.thesimplecloud.simplecloud.api.process.onlineonfiguration.IProcessesOnlineCountConfiguration
import eu.thesimplecloud.simplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.simplecloud.api.service.*
import eu.thesimplecloud.simplecloud.api.template.ITemplate
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 27.03.2021
 * Time: 12:01
 * @author Frederick Baier
 *
 * Represents a process group exposed by the api to allow easier accessibility of associated objects
*/
abstract class AbstractCloudProcessGroup constructor(
    private val configuration: AbstractCloudProcessGroupConfiguration,
    private val templateService: ITemplateService,
    private val processVersionService: IProcessVersionService,
    private val jvmArgumentsService: IJvmArgumentsService,
    private val processOnlineCountService: IProcessOnlineCountService,
    private val nodeService: INodeService,
    private val processService: ICloudProcessService,
) : ICloudProcessGroup {


    override fun getMaxMemory(): Int {
        return this.configuration.maxMemory
    }

    override fun getMaxPlayers(): Int {
        return this.configuration.maxPlayers
    }

    override fun getOnlinePlayerCount(): Int {
        TODO()
    }

    override fun isInMaintenance(): Boolean {
        return this.configuration.maintenance
    }

    override fun getTemplateName(): String {
        return this.configuration.templateName
    }

    override fun getTemplate(): CompletableFuture<ITemplate> {
        return this.templateService.findByName(this.configuration.templateName)
    }

    override fun getProcessVersionName(): String {
        return this.configuration.versionName
    }

    override fun getVersion(): CompletableFuture<IProcessVersion> {
        return this.processVersionService.findByName(this.configuration.versionName)
    }

    override fun getJvmArgumentsName(): String? {
        return this.configuration.jvmArgumentName
    }

    override fun getJvmArguments(): CompletableFuture<IJVMArguments> {
        val jvmArgumentName = this.configuration.jvmArgumentName
            ?: return CompletableFuture.failedFuture(NoSuchElementException())
        return this.jvmArgumentsService.findByName(jvmArgumentName)
    }

    override fun getProcessOnlineCountConfigurationName(): String {
        return this.configuration.onlineCountConfigurationName
    }

    override fun getProcessOnlineCountConfiguration(): CompletableFuture<IProcessesOnlineCountConfiguration> {
        return this.processOnlineCountService.findByName(this.configuration.onlineCountConfigurationName)
    }

    override fun getJoinPermission(): String? {
        return this.configuration.joinPermission
    }

    override fun getMinimumOnlineProcessCount(): Int {
        return this.configuration.minimumProcessCount
    }

    override fun getMaximumOnlineProcessCount(): Int {
        return this.configuration.maximumProcessCount
    }

    override fun isStatic(): Boolean {
        return this.configuration.static
    }

    override fun isStateUpdatingEnabled(): Boolean {
        return this.configuration.stateUpdating
    }

    override fun getStartPriority(): Int {
        return this.configuration.startPriority
    }

    override fun getNodeNamesAllowedToStartServicesOn(): List<String> {
        return this.configuration.nodeNamesAllowedToStartOn
    }

    override fun getNodesAllowedToStartServicesOn(): CompletableFuture<List<INode>> {
        return this.nodeService.findNodesByName(*this.configuration.nodeNamesAllowedToStartOn.toTypedArray())
    }

    override fun getProcesses(): CompletableFuture<List<ICloudProcess>> {
        return this.processService.findProcessesByGroup(this)
    }

    override fun getName(): String {
        return this.configuration.name
    }

    override fun getIdentifier(): String {
        return getName()
    }

}