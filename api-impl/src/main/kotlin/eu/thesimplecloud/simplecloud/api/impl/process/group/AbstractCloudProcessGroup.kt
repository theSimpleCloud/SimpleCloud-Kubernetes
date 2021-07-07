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

import eu.thesimplecloud.simplecloud.api.CloudAPI
import eu.thesimplecloud.simplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.simplecloud.api.node.INode
import eu.thesimplecloud.simplecloud.api.process.ICloudProcess
import eu.thesimplecloud.simplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.simplecloud.api.process.onlineonfiguration.IProcessesOnlineCountConfiguration
import eu.thesimplecloud.simplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.simplecloud.api.template.ITemplate
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 27.03.2021
 * Time: 12:01
 * @author Frederick Baier
 */
abstract class AbstractCloudProcessGroup(
    private val name: String,
    private val maxMemory: Int,
    private val maxPlayers: Int,
    private val maintenance: Boolean,
    private val minimumProcessCount: Int,
    private val maximumProcessCount: Int,
    private val templateName: String,
    private val jvmArgumentName: String?,
    private val versionName: String,
    private val onlineCountConfigurationName: String,
    private val static: Boolean,
    private val stateUpdating: Boolean,
    private val startPriority: Int,
    private val joinPermission: String?,
    private val nodeNamesAllowedToStartOn: List<String>
) : ICloudProcessGroup {


    private val onlineCount: Int = 0

    override fun getMaxMemory(): Int {
        return this.maxMemory
    }

    override fun getMaxPlayers(): Int {
        return this.maxPlayers
    }

    override fun getOnlinePlayerCount(): Int {
        return this.onlineCount
    }

    override fun isInMaintenance(): Boolean {
        return this.maintenance
    }

    override fun getTemplateName(): String {
        return this.templateName
    }

    override fun getTemplate(): CompletableFuture<ITemplate> {
        return CloudAPI.instance.getTemplateService().findByName(this.templateName)
    }

    override fun getProcessVersionName(): String {
        return this.versionName
    }

    override fun getVersion(): CompletableFuture<IProcessVersion> {
        return CloudAPI.instance.getProcessVersionService().findByName(this.versionName)
    }

    override fun getJvmArgumentsName(): String? {
        return this.jvmArgumentName
    }

    override fun getJvmArguments(): CompletableFuture<IJVMArguments> {
        if (this.jvmArgumentName == null) return CompletableFuture.failedFuture(NoSuchElementException())
        return CloudAPI.instance.getJvmArgumentsService().findByName(this.jvmArgumentName)
    }

    override fun getProcessOnlineCountConfigurationName(): String {
        return this.onlineCountConfigurationName
    }

    override fun getProcessOnlineCountConfiguration(): CompletableFuture<IProcessesOnlineCountConfiguration> {
        return CloudAPI.instance.getProcessOnlineCountService().findByName(this.onlineCountConfigurationName)
    }

    override fun getJoinPermission(): String? {
        return this.joinPermission
    }

    override fun getMinimumOnlineProcessCount(): Int {
        return this.minimumProcessCount
    }

    override fun getMaximumOnlineProcessCount(): Int {
        return this.maximumProcessCount
    }

    override fun isStatic(): Boolean {
        return this.static
    }

    override fun isStateUpdatingEnabled(): Boolean {
        return this.stateUpdating
    }

    override fun getStartPriority(): Int {
        return this.startPriority
    }

    override fun getNodeNamesAllowedToStartServicesOn(): List<String> {
        return this.nodeNamesAllowedToStartOn
    }

    override fun getNodesAllowedToStartServicesOn(): CompletableFuture<List<INode>> {
        return CloudAPI.instance.getNodeService().findNodesByName(*this.nodeNamesAllowedToStartOn.toTypedArray())
    }

    override fun getProcesses(): CompletableFuture<List<ICloudProcess>> {
        return CloudAPI.instance.getProcessService().findProcessesByGroup(this)
    }

    override fun getName(): String {
        return this.name
    }

    override fun getIdentifier(): String {
        return getName()
    }

}