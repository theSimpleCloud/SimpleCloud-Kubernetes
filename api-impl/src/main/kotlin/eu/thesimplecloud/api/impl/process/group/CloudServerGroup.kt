package eu.thesimplecloud.api.impl.process.group

import eu.thesimplecloud.api.process.ProcessGroupType
import eu.thesimplecloud.api.process.group.server.ICloudServerGroup
import eu.thesimplecloud.api.process.group.update.ICloudProcessGroupUpdateRequest
import eu.thesimplecloud.api.impl.process.request.CloudServerGroupUpdateRequest

/**
 * Created by IntelliJ IDEA.
 * Date: 05.04.2021
 * Time: 22:17
 * @author Frederick Baier
 */
open class CloudServerGroup(
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
    joinPermission: String?
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
), ICloudServerGroup {

    override fun createUpdateRequest(): ICloudProcessGroupUpdateRequest {
        return CloudServerGroupUpdateRequest(this)
    }

    override fun getProcessGroupType(): ProcessGroupType {
        return ProcessGroupType.SERVER
    }

}