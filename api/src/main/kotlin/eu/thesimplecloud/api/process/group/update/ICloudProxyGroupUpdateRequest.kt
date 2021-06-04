package eu.thesimplecloud.api.process.group.update

import eu.thesimplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.api.process.group.proxy.ICloudProxyGroup
import eu.thesimplecloud.api.process.onlineonfiguration.IProcessesOnlineCountConfiguration
import eu.thesimplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.api.template.ITemplate
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

    override fun setJvmArguments(jvmArguments: IJVMArguments): ICloudProxyGroupUpdateRequest

    override fun setJvmArguments(jvmArgumentsFuture: CompletableFuture<IJVMArguments>): ICloudProxyGroupUpdateRequest

    override fun setOnlineCountConfiguration(onlineCountConfiguration: IProcessesOnlineCountConfiguration): ICloudProxyGroupUpdateRequest

    override fun setOnlineCountConfiguration(onlineCountConfigurationFuture: CompletableFuture<IProcessesOnlineCountConfiguration>): ICloudProxyGroupUpdateRequest

    override fun setMaintenance(maintenance: Boolean): ICloudProxyGroupUpdateRequest

    override fun setMinimumOnlineProcessCount(minCount: Int): ICloudProxyGroupUpdateRequest

    override fun setMaximumOnlineProcessCount(maxCount: Int): ICloudProxyGroupUpdateRequest

    override fun setJoinPermission(permission: String?): ICloudProxyGroupUpdateRequest

    override fun setStateUpdating(stateUpdating: Boolean): ICloudProxyGroupUpdateRequest

    override fun setStartPriority(priority: Int): ICloudProxyGroupUpdateRequest

    override fun submit(): CompletableFuture<ICloudProcessGroup>

}