package eu.thesimplecloud.api.impl.process.request

import eu.thesimplecloud.api.internal.InternalCloudAPI
import eu.thesimplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.api.impl.process.group.CloudLobbyGroup
import eu.thesimplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.api.process.group.lobby.ICloudLobbyGroup
import eu.thesimplecloud.api.process.group.update.ICloudLobbyGroupUpdateRequest
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
class CloudLobbyGroupUpdateRequest(private val lobbyGroup: ICloudLobbyGroup) :
    CloudServerGroupUpdateRequest(lobbyGroup),
    ICloudLobbyGroupUpdateRequest {

     @Volatile
     private var lobbyPriority = this.lobbyGroup.getLobbyPriority()

    override fun setLobbyPriority(lobbyPriority: Int): ICloudLobbyGroupUpdateRequest {
        this.lobbyPriority = lobbyPriority
        return this
    }

    override fun getProcessGroup(): ICloudLobbyGroup {
        return this.lobbyGroup
    }

    override fun setMaxMemory(memory: Int): ICloudLobbyGroupUpdateRequest {
        super.setMaxMemory(memory)
        return this
    }

    override fun setMaxPlayers(players: Int): ICloudLobbyGroupUpdateRequest {
        super.setMaxPlayers(players)
        return this
    }

    override fun setVersion(version: IProcessVersion): ICloudLobbyGroupUpdateRequest {
        super.setVersion(version)
        return this
    }

    override fun setVersion(versionFuture: CompletableFuture<IProcessVersion>): ICloudLobbyGroupUpdateRequest {
        super.setVersion(versionFuture)
        return this
    }

    override fun setTemplate(template: ITemplate): ICloudLobbyGroupUpdateRequest {
        super.setTemplate(template)
        return this
    }

    override fun setTemplate(templateFuture: CompletableFuture<ITemplate>): ICloudLobbyGroupUpdateRequest {
        super.setTemplate(templateFuture)
        return this
    }

    override fun setJvmArguments(jvmArguments: IJVMArguments): ICloudLobbyGroupUpdateRequest {
        super.setJvmArguments(jvmArguments)
        return this
    }

    override fun setJvmArguments(jvmArgumentsFuture: CompletableFuture<IJVMArguments>): ICloudLobbyGroupUpdateRequest {
        super.setJvmArguments(jvmArgumentsFuture)
        return this
    }

    override fun setOnlineCountConfiguration(onlineCountConfiguration: IProcessesOnlineCountConfiguration): ICloudLobbyGroupUpdateRequest {
        super.setOnlineCountConfiguration(onlineCountConfiguration)
        return this
    }

    override fun setOnlineCountConfiguration(onlineCountConfigurationFuture: CompletableFuture<IProcessesOnlineCountConfiguration>): ICloudLobbyGroupUpdateRequest {
        super.setOnlineCountConfiguration(onlineCountConfigurationFuture)
        return this
    }

    override fun setMaintenance(maintenance: Boolean): ICloudLobbyGroupUpdateRequest {
        super.setMaintenance(maintenance)
        return this
    }

    override fun setMinimumOnlineProcessCount(minCount: Int): ICloudLobbyGroupUpdateRequest {
        super.setMinimumOnlineProcessCount(minCount)
        return this
    }

    override fun setMaximumOnlineProcessCount(maxCount: Int): ICloudLobbyGroupUpdateRequest {
        super.setMaximumOnlineProcessCount(maxCount)
        return this
    }

    override fun setJoinPermission(permission: String?): ICloudLobbyGroupUpdateRequest {
        super.setJoinPermission(permission)
        return this
    }

    override fun setStateUpdating(stateUpdating: Boolean): ICloudLobbyGroupUpdateRequest {
        super.setStateUpdating(stateUpdating)
        return this
    }

    override fun setStartPriority(priority: Int): ICloudLobbyGroupUpdateRequest {
        super.setStartPriority(priority)
        return this
    }

    override fun submit0(
        version: IProcessVersion,
        template: ITemplate,
        jvmArguments: IJVMArguments?,
        onlineCountConfiguration: IProcessesOnlineCountConfiguration
    ): CompletableFuture<ICloudProcessGroup> {
        val lobbyGroup = CloudLobbyGroup(
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
            this.lobbyPriority
        )
        return InternalCloudAPI.instance.getProcessGroupService().updateGroup(lobbyGroup)
    }
}