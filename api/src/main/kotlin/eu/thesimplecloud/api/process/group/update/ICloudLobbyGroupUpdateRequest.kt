package eu.thesimplecloud.api.process.group.update

import eu.thesimplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.api.process.group.lobby.ICloudLobbyGroup
import eu.thesimplecloud.api.process.onlineonfiguration.IProcessesOnlineCountConfiguration
import eu.thesimplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.api.template.ITemplate
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 06.04.2021
 * Time: 09:59
 * @author Frederick Baier
 */
interface ICloudLobbyGroupUpdateRequest : ICloudServerGroupUpdateRequest {

    /**
     * Sets the lobby priority for the group
     * @return this
     */
    fun setLobbyPriority(lobbyPriority: Int): ICloudLobbyGroupUpdateRequest

    override fun getProcessGroup(): ICloudLobbyGroup

    override fun setMaxMemory(memory: Int): ICloudLobbyGroupUpdateRequest

    override fun setMaxPlayers(players: Int): ICloudLobbyGroupUpdateRequest

    override fun setVersion(version: IProcessVersion): ICloudLobbyGroupUpdateRequest

    override fun setVersion(versionFuture: CompletableFuture<IProcessVersion>): ICloudLobbyGroupUpdateRequest

    override fun setTemplate(template: ITemplate): ICloudLobbyGroupUpdateRequest

    override fun setTemplate(templateFuture: CompletableFuture<ITemplate>): ICloudLobbyGroupUpdateRequest

    override fun setJvmArguments(jvmArguments: IJVMArguments): ICloudLobbyGroupUpdateRequest

    override fun setJvmArguments(jvmArgumentsFuture: CompletableFuture<IJVMArguments>): ICloudLobbyGroupUpdateRequest

    override fun setOnlineCountConfiguration(onlineCountConfiguration: IProcessesOnlineCountConfiguration): ICloudLobbyGroupUpdateRequest

    override fun setOnlineCountConfiguration(onlineCountConfigurationFuture: CompletableFuture<IProcessesOnlineCountConfiguration>): ICloudLobbyGroupUpdateRequest

    override fun setMaintenance(maintenance: Boolean): ICloudLobbyGroupUpdateRequest

    override fun setMinimumOnlineProcessCount(minCount: Int): ICloudLobbyGroupUpdateRequest

    override fun setMaximumOnlineProcessCount(maxCount: Int): ICloudLobbyGroupUpdateRequest

    override fun setJoinPermission(permission: String?): ICloudLobbyGroupUpdateRequest

    override fun setStateUpdating(stateUpdating: Boolean): ICloudLobbyGroupUpdateRequest

    override fun setStartPriority(priority: Int): ICloudLobbyGroupUpdateRequest

    override fun submit(): CompletableFuture<ICloudProcessGroup>
}