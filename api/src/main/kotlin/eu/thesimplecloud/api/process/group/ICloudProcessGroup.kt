package eu.thesimplecloud.api.process.group

import eu.thesimplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.api.node.INode
import eu.thesimplecloud.api.process.ICloudProcess
import eu.thesimplecloud.api.process.ProcessGroupType
import eu.thesimplecloud.api.process.group.update.ICloudProcessGroupUpdateRequest
import eu.thesimplecloud.api.process.onlineonfiguration.IProcessesOnlineCountConfiguration
import eu.thesimplecloud.api.process.request.IProcessGroupDeleteRequest
import eu.thesimplecloud.api.process.request.IProcessStartRequest
import eu.thesimplecloud.api.process.template.ITemplate
import eu.thesimplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.api.reference.INamedReferenceHolder
import eu.thesimplecloud.api.utils.INameable

interface ICloudProcessGroup : INameable {

    fun getMaxMemory(): Int

    fun getMaxPlayers(): Int

    fun getOnlinePlayerCount(): Int

    fun isInMaintenance(): Boolean

    fun getTemplate(): ITemplate

    fun getVersion(): IProcessVersion

    fun getJvmArguments(): IJVMArguments

    fun getProcessGroupType(): ProcessGroupType

    fun getJoinPermission(): String?

    fun getMinimumOnlineProcessCount(): Int

    fun getMaximumOnlineProcessCount(): Int

    fun isStatic(): Boolean

    fun isStateUpdatingEnabled(): Boolean

    fun getStartPriority(): Int

    fun getNodesAllowedToStartServicesOn(): List<INode>

    fun getProcesses(): List<ICloudProcess>

    fun getProcessOnlineCountConfiguration(): IProcessesOnlineCountConfiguration

    fun createProcessStartRequest(): IProcessStartRequest

    fun createUpdateRequest(): ICloudProcessGroupUpdateRequest

    fun createDeleteRequest(): IProcessGroupDeleteRequest

}