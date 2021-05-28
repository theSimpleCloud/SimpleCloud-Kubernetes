package eu.thesimplecloud.api.impl.process.group

import eu.thesimplecloud.api.impl.process.request.CloudLobbyGroupUpdateRequest
import eu.thesimplecloud.api.process.ProcessGroupType
import eu.thesimplecloud.api.process.group.lobby.ICloudLobbyGroup
import eu.thesimplecloud.api.process.group.update.ICloudLobbyGroupUpdateRequest
import eu.thesimplecloud.api.process.group.update.ICloudProcessGroupUpdateRequest

/**
 * Created by IntelliJ IDEA.
 * Date: 06.04.2021
 * Time: 09:59
 * @author Frederick Baier
 */
class CloudLobbyGroup(
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
    nodeNamesAllowedToStartOn: List<String>,
    private val lobbyPriority: Int
) : CloudServerGroup(
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
    joinPermission,
    nodeNamesAllowedToStartOn
), ICloudLobbyGroup {

    override fun getLobbyPriority(): Int {
        return this.lobbyPriority
    }

    override fun createUpdateRequest(): ICloudLobbyGroupUpdateRequest {
        return CloudLobbyGroupUpdateRequest(this)
    }

    override fun getProcessGroupType(): ProcessGroupType {
        return ProcessGroupType.LOBBY
    }

}