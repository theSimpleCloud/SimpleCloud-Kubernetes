package eu.thesimplecloud.api.impl.process.group

import eu.thesimplecloud.api.process.ProcessGroupType
import eu.thesimplecloud.api.process.group.proxy.ICloudProxyGroup
import eu.thesimplecloud.api.process.group.update.ICloudProcessGroupUpdateRequest
import eu.thesimplecloud.api.impl.process.request.CloudProxyGroupUpdateRequest
import eu.thesimplecloud.api.process.group.update.ICloudProxyGroupUpdateRequest

/**
 * Created by IntelliJ IDEA.
 * Date: 06.04.2021
 * Time: 11:23
 * @author Frederick Baier
 */
class CloudProxyGroup(
    name: String,
    maxMemory: Int,
    maxPlayers: Int,
    maintenance: Boolean,
    minimumProcessCount: Int,
    maximumProcessCount: Int,
    templateName: String,
    jvmArgumentName: String?,
    versionName: String,
    onlineCountConfigurationName: String,
    static: Boolean,
    stateUpdating: Boolean,
    startPriority: Int,
    joinPermission: String?,
    private val startPort: Int
) : AbstractCloudProcessGroup(
    name,
    maxMemory,
    maxPlayers,
    maintenance,
    minimumProcessCount,
    maximumProcessCount,
    templateName,
    jvmArgumentName,
    versionName,
    onlineCountConfigurationName,
    static,
    stateUpdating,
    startPriority,
    joinPermission
), ICloudProxyGroup {

    override fun getStartPort(): Int {
        return this.startPort
    }

    override fun getProcessGroupType(): ProcessGroupType {
        return ProcessGroupType.PROXY
    }

    override fun createUpdateRequest(): ICloudProxyGroupUpdateRequest {
        return CloudProxyGroupUpdateRequest(this)
    }

}