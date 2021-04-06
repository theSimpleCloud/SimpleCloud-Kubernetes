package eu.thesimplecloud.api.process.group.update

import eu.thesimplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.api.process.onlineonfiguration.IProcessesOnlineCountConfiguration
import eu.thesimplecloud.api.template.ITemplate
import eu.thesimplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.api.utils.IRequest
import eu.thesimplecloud.api.process.state.ProcessState
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 17.03.2021
 * Time: 18:59
 * @author Frederick Baier
 *
 * Request for updating a registered group
 *
 */
interface ICloudProcessGroupUpdateRequest : IRequest<ICloudProcessGroup> {

    /**
     * Returns the group this request updates
     */
    fun getProcessGroup(): ICloudProcessGroup

    /**
     * Sets the maximum amount of memory
     * @return this
     */
    fun setMaxMemory(memory: Int): ICloudProcessGroupUpdateRequest

    /**
     * Sets the maximum amount of players
     * @return this
     */
    fun setMaxPlayers(players: Int): ICloudProcessGroupUpdateRequest

    /**
     * Sets the version for the group
     * @return this
     */
    fun setVersion(version: IProcessVersion): ICloudProcessGroupUpdateRequest

    /**
     * Sets the version for the group
     * @return this
     */
    fun setVersion(versionFuture: CompletableFuture<IProcessVersion>): ICloudProcessGroupUpdateRequest

    /**
     * Sets the template for the group
     * @return this
     */
    fun setTemplate(template: ITemplate): ICloudProcessGroupUpdateRequest

    /**
     * Sets the template for the group
     * @return this
     */
    fun setTemplate(templateFuture: CompletableFuture<ITemplate>): ICloudProcessGroupUpdateRequest

    /**
     * Sets the jvm arguments for the group
     * @return this
     */
    fun setJvmArguments(jvmArguments: IJVMArguments): ICloudProcessGroupUpdateRequest

    /**
     * Sets the jvm arguments for the group
     * @return this
     */
    fun setJvmArguments(jvmArgumentsFuture: CompletableFuture<IJVMArguments>): ICloudProcessGroupUpdateRequest

    /**
     * Sets the online count configuration for the group
     * @return this
     */
    fun setOnlineCountConfiguration(onlineCountConfiguration: IProcessesOnlineCountConfiguration): ICloudProcessGroupUpdateRequest

    /**
     * Sets the online count configuration for the group
     * @return this
     */
    fun setOnlineCountConfiguration(onlineCountConfigurationFuture: CompletableFuture<IProcessesOnlineCountConfiguration>): ICloudProcessGroupUpdateRequest

    /**
     * Sets the maintenance state for the group
     * @return this
     */
    fun setMaintenance(maintenance: Boolean): ICloudProcessGroupUpdateRequest

    /**
     * Sets the minimum count of processes to be VISIBLE
     * @return this
     */
    fun setMinimumOnlineProcessCount(minCount: Int): ICloudProcessGroupUpdateRequest

    /**
     * Sets the maximum count of processes to be VISIBLE
     * @return this
     */
    fun setMaximumOnlineProcessCount(maxCount: Int): ICloudProcessGroupUpdateRequest

    /**
     * Sets the permission a player need to join processes of the group
     * @return this
     */
    fun setJoinPermission(permission: String?): ICloudProcessGroupUpdateRequest

    /**
     * Sets whether the state of processes shall be automatically set to [ProcessState.VISIBLE]
     *  after the process has been started
     * @return this
     */
    fun setStateUpdating(stateUpdating: Boolean): ICloudProcessGroupUpdateRequest

    /**
     * Sets start priority for the group (higher will be started first)
     * @return this
     */
    fun setStartPriority(priority: Int): ICloudProcessGroupUpdateRequest



}