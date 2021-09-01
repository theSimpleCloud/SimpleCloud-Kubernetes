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

import com.ea.async.Async.await
import eu.thesimplecloud.simplecloud.api.future.nonNull
import eu.thesimplecloud.simplecloud.api.future.nullable
import eu.thesimplecloud.simplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.simplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.simplecloud.api.request.group.update.ICloudProcessGroupUpdateRequest
import eu.thesimplecloud.simplecloud.api.process.onlineonfiguration.IProcessesOnlineCountConfiguration
import eu.thesimplecloud.simplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.simplecloud.api.template.ITemplate
import eu.thesimplecloud.simplecloud.api.utils.future.CloudCompletableFuture
import org.checkerframework.checker.units.qual.C
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 04.04.2021
 * Time: 21:35
 * @author Frederick Baier
 */
abstract class AbstractCloudProcessGroupUpdateRequest(
    private val processGroup: ICloudProcessGroup
) : ICloudProcessGroupUpdateRequest {

    @Volatile
    protected var maxMemory: Int = this.processGroup.getMaxMemory()

    @Volatile
    protected var maxPlayers: Int = this.processGroup.getMaxPlayers()

    @Volatile
    protected var maintenance: Boolean = this.processGroup.isInMaintenance()

    @Volatile
    protected var minProcessCount: Int = this.processGroup.getMinimumOnlineProcessCount()

    @Volatile
    protected var maxProcessCount: Int = this.processGroup.getMaximumOnlineProcessCount()

    @Volatile
    protected var joinPermission: String? = this.processGroup.getJoinPermission()

    @Volatile
    protected var stateUpdating: Boolean = this.processGroup.isStateUpdatingEnabled()

    @Volatile
    protected var startPriority: Int = this.processGroup.getStartPriority()

    @Volatile
    protected var versionFuture: CompletableFuture<IProcessVersion> = this.processGroup.getVersion()

    @Volatile
    protected var templateFuture: CompletableFuture<ITemplate> = this.processGroup.getTemplate()

    @Volatile
    protected var jvmArgumentsFuture: CompletableFuture<IJVMArguments?> = this.processGroup.getJvmArguments() as CompletableFuture<IJVMArguments?>

    @Volatile
    protected var onlineCountConfigurationFuture: CompletableFuture<IProcessesOnlineCountConfiguration> =
        this.processGroup.getProcessOnlineCountConfiguration()

    @Volatile
    protected var nodesAllowedToStartOn: List<String> = this.processGroup.getNodeNamesAllowedToStartServicesOn()

    override fun getProcessGroup(): ICloudProcessGroup {
        return this.processGroup
    }

    override fun setMaxMemory(memory: Int): ICloudProcessGroupUpdateRequest {
        this.maxMemory = memory
        return this
    }

    override fun setMaxPlayers(players: Int): ICloudProcessGroupUpdateRequest {
        this.maxPlayers = players
        return this
    }

    override fun setVersion(version: IProcessVersion): ICloudProcessGroupUpdateRequest {
        this.versionFuture = CloudCompletableFuture.completedFuture(version)
        return this
    }

    override fun setVersion(versionFuture: CompletableFuture<IProcessVersion>): ICloudProcessGroupUpdateRequest {
        this.versionFuture = versionFuture
        return this
    }

    override fun setTemplate(template: ITemplate): ICloudProcessGroupUpdateRequest {
        this.templateFuture = CloudCompletableFuture.completedFuture(template)
        return this
    }

    override fun setTemplate(templateFuture: CompletableFuture<ITemplate>): ICloudProcessGroupUpdateRequest {
        this.templateFuture = templateFuture
        return this
    }

    override fun setJvmArguments(jvmArguments: IJVMArguments?): ICloudProcessGroupUpdateRequest {
        this.jvmArgumentsFuture = CloudCompletableFuture.completedFuture(jvmArguments)
        return this
    }

    override fun setJvmArguments(jvmArgumentsFuture: CompletableFuture<IJVMArguments>): ICloudProcessGroupUpdateRequest {
        this.jvmArgumentsFuture = jvmArgumentsFuture as CompletableFuture<IJVMArguments?>
        return this
    }

    override fun setOnlineCountConfiguration(onlineCountConfiguration: IProcessesOnlineCountConfiguration): ICloudProcessGroupUpdateRequest {
        this.onlineCountConfigurationFuture = CloudCompletableFuture.completedFuture(onlineCountConfiguration)
        return this
    }

    override fun setOnlineCountConfiguration(onlineCountConfigurationFuture: CompletableFuture<IProcessesOnlineCountConfiguration>): ICloudProcessGroupUpdateRequest {
        this.onlineCountConfigurationFuture = onlineCountConfigurationFuture
        return this
    }

    override fun setNodesAllowedToStartOn(nodes: List<String>): ICloudProcessGroupUpdateRequest {
        this.nodesAllowedToStartOn = nodes
        return this
    }

    override fun setMaintenance(maintenance: Boolean): ICloudProcessGroupUpdateRequest {
        this.maintenance = maintenance
        return this
    }

    override fun setMinimumOnlineProcessCount(minCount: Int): ICloudProcessGroupUpdateRequest {
        this.minProcessCount = minCount
        return this
    }

    override fun setMaximumOnlineProcessCount(maxCount: Int): ICloudProcessGroupUpdateRequest {
        this.maxProcessCount = maxCount
        return this
    }

    override fun setJoinPermission(permission: String?): ICloudProcessGroupUpdateRequest {
        this.joinPermission = permission
        return this
    }

    override fun setStateUpdating(stateUpdating: Boolean): ICloudProcessGroupUpdateRequest {
        this.stateUpdating = stateUpdating
        return this
    }

    override fun setStartPriority(priority: Int): ICloudProcessGroupUpdateRequest {
        this.startPriority = priority
        return this
    }

    override fun submit(): CompletableFuture<ICloudProcessGroup> {
        val version = await(this.versionFuture)
        val template = await(this.templateFuture)
        val jvmArguments = await(this.jvmArgumentsFuture.nullable())
        val onlineCountConfiguration = await(this.onlineCountConfigurationFuture)
        val nodesAllowedToStartOn = this.nodesAllowedToStartOn
        return submit0(
            version,
            template,
            jvmArguments,
            onlineCountConfiguration,
            nodesAllowedToStartOn
        )
    }

    abstract fun submit0(
        version: IProcessVersion,
        template: ITemplate,
        jvmArguments: IJVMArguments?,
        onlineCountConfiguration: IProcessesOnlineCountConfiguration,
        nodesAllowedToStartOn: List<String>
    ): CompletableFuture<ICloudProcessGroup>


}