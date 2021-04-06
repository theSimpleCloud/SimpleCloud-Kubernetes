package eu.thesimplecloud.api.impl.process.request

import eu.thesimplecloud.api.impl.process.group.CloudProxyGroup
import eu.thesimplecloud.api.internal.InternalCloudAPI
import eu.thesimplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.api.process.group.proxy.ICloudProxyGroup
import eu.thesimplecloud.api.process.group.update.ICloudProxyGroupUpdateRequest
import eu.thesimplecloud.api.process.onlineonfiguration.IProcessesOnlineCountConfiguration
import eu.thesimplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.api.template.ITemplate
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 06.04.2021
 * Time: 10:03
 * @author Frederick Baier
 */
class CloudProxyGroupUpdateRequest(private val proxyGroup: ICloudProxyGroup) :
    AbstractCloudProcessGroupUpdateRequest(proxyGroup),
    ICloudProxyGroupUpdateRequest {

    @Volatile
    private var startPort = this.proxyGroup.getStartPort()

    override fun setStartPort(startPort: Int): ICloudProxyGroupUpdateRequest {
        this.startPort = startPort
        return this
    }

    override fun getProcessGroup(): ICloudProxyGroup {
        return this.proxyGroup
    }

    override fun setMaxMemory(memory: Int): ICloudProxyGroupUpdateRequest {
        super.setMaxMemory(memory)
        return this
    }

    override fun setMaxPlayers(players: Int): ICloudProxyGroupUpdateRequest {
        super.setMaxPlayers(players)
        return this
    }

    override fun setVersion(version: IProcessVersion): ICloudProxyGroupUpdateRequest {
        super.setVersion(version)
        return this
    }

    override fun setVersion(versionFuture: CompletableFuture<IProcessVersion>): ICloudProxyGroupUpdateRequest {
        super.setVersion(versionFuture)
        return this
    }

    override fun setTemplate(template: ITemplate): ICloudProxyGroupUpdateRequest {
        super.setTemplate(template)
        return this
    }

    override fun setTemplate(templateFuture: CompletableFuture<ITemplate>): ICloudProxyGroupUpdateRequest {
        super.setTemplate(templateFuture)
        return this
    }

    override fun setJvmArguments(jvmArguments: IJVMArguments): ICloudProxyGroupUpdateRequest {
        super.setJvmArguments(jvmArguments)
        return this
    }

    override fun setJvmArguments(jvmArgumentsFuture: CompletableFuture<IJVMArguments>): ICloudProxyGroupUpdateRequest {
        super.setJvmArguments(jvmArgumentsFuture)
        return this
    }

    override fun setOnlineCountConfiguration(onlineCountConfiguration: IProcessesOnlineCountConfiguration): ICloudProxyGroupUpdateRequest {
        super.setOnlineCountConfiguration(onlineCountConfiguration)
        return this
    }

    override fun setOnlineCountConfiguration(onlineCountConfigurationFuture: CompletableFuture<IProcessesOnlineCountConfiguration>): ICloudProxyGroupUpdateRequest {
        super.setOnlineCountConfiguration(onlineCountConfigurationFuture)
        return this
    }

    override fun setMaintenance(maintenance: Boolean): ICloudProxyGroupUpdateRequest {
        super.setMaintenance(maintenance)
        return this
    }

    override fun setMinimumOnlineProcessCount(minCount: Int): ICloudProxyGroupUpdateRequest {
        super.setMinimumOnlineProcessCount(minCount)
        return this
    }

    override fun setMaximumOnlineProcessCount(maxCount: Int): ICloudProxyGroupUpdateRequest {
        super.setMaximumOnlineProcessCount(maxCount)
        return this
    }

    override fun setJoinPermission(permission: String?): ICloudProxyGroupUpdateRequest {
        super.setJoinPermission(permission)
        return this
    }

    override fun setStateUpdating(stateUpdating: Boolean): ICloudProxyGroupUpdateRequest {
        super.setStateUpdating(stateUpdating)
        return this
    }

    override fun setStartPriority(priority: Int): ICloudProxyGroupUpdateRequest {
        super.setStartPriority(priority)
        return this
    }

    override fun submit0(
        version: IProcessVersion,
        template: ITemplate,
        jvmArguments: IJVMArguments?,
        onlineCountConfiguration: IProcessesOnlineCountConfiguration
    ): CompletableFuture<ICloudProcessGroup> {
        val proxyGroup = CloudProxyGroup(
            getProcessGroup().getName(),
            this.maxMemory,
            this.maxPlayers,
            this.maintenance,
            this.minProcessCount,
            this.maxProcessCount,
            template.getName(),
            jvmArguments?.getName(),
            version.getName(),
            onlineCountConfiguration.getName(),
            getProcessGroup().isStatic(),
            this.stateUpdating,
            this.startPriority,
            this.joinPermission,
            this.startPort
        )
        return InternalCloudAPI.instance.getProcessGroupService().updateGroup(proxyGroup)
    }
}