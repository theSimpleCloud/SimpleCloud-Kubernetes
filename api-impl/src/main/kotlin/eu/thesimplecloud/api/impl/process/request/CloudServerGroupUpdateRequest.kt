package eu.thesimplecloud.api.impl.process.request

import eu.thesimplecloud.api.internal.InternalCloudAPI
import eu.thesimplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.api.impl.process.group.CloudServerGroup
import eu.thesimplecloud.api.impl.process.request.AbstractCloudProcessGroupUpdateRequest
import eu.thesimplecloud.api.node.INode
import eu.thesimplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.api.process.group.server.ICloudServerGroup
import eu.thesimplecloud.api.process.group.update.ICloudServerGroupUpdateRequest
import eu.thesimplecloud.api.process.onlineonfiguration.IProcessesOnlineCountConfiguration
import eu.thesimplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.api.template.ITemplate
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 05.04.2021
 * Time: 22:03
 * @author Frederick Baier
 */
open class CloudServerGroupUpdateRequest(serverGroup: ICloudServerGroup) :
    AbstractCloudProcessGroupUpdateRequest(serverGroup),
    ICloudServerGroupUpdateRequest {

    override fun submit0(
        version: IProcessVersion,
        template: ITemplate,
        jvmArguments: IJVMArguments?,
        onlineCountConfiguration: IProcessesOnlineCountConfiguration,
        nodesAllowedToStartOn: List<INode>
    ): CompletableFuture<ICloudProcessGroup> {
        val serverGroup = CloudServerGroup(
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
            nodesAllowedToStartOn.map { it.getName() }
        )
        return InternalCloudAPI.instance.getProcessGroupService().updateGroup(serverGroup)
    }
}