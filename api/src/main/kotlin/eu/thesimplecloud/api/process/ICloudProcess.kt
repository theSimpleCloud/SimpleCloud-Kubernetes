package eu.thesimplecloud.api.process

import eu.thesimplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.api.node.INode
import eu.thesimplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.api.process.request.IProcessStopRequest
import eu.thesimplecloud.api.process.state.ProcessState
import eu.thesimplecloud.api.template.ITemplate
import eu.thesimplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.api.repository.IIdentifiable
import eu.thesimplecloud.api.utils.Address
import eu.thesimplecloud.api.utils.INetworkComponent
import java.util.*
import java.util.concurrent.CompletableFuture

interface ICloudProcess : INetworkComponent, IIdentifiable<String> {

    /**
     * Returns the unique id of the process
     */
    fun getUniqueId(): UUID

    /**
     * Returns the group of the process
     */
    fun getGroup(): CompletableFuture<ICloudProcessGroup>

    /**
     * Returns the process number
     * e.g The name is Lobby-2 -> 2 would be the process number
     */
    fun getProcessNumber(): Int

    /**
     * Returns the state of the process
     */
    fun getState(): ProcessState

    /**
     * Returns the maximum amount of memory this process can use in MB
     */
    fun getMaxMemory(): Int

    /**
     * Returns the amount of memory the process currently uses in MB
     */
    fun getUsedMemory(): Int

    /**
     * Returns the maximum amount of players that can be simultaneously connected the process
     */
    fun getMaxPlayers(): Int

    /**
     * Returns the address of the process
     */
    fun getAddress(): Address

    /**
     * Returns whether the process is static
     */
    fun isStatic(): Boolean

    /**
     * Returns the process type
     */
    fun getProcessType(): ProcessGroupType

    /**
     * Returns the version the process is running with
     */
    fun getVersion(): CompletableFuture<IProcessVersion>

    /**
     * Returns the template this process was started from
     */
    fun getTemplate(): CompletableFuture<ITemplate>

    /**
     * Returns the [IJVMArguments] the process used to start
     */
    fun getJvmArguments(): CompletableFuture<IJVMArguments>

    /**
     * Returns the node this process is running on
     */
    fun getNodeRunningOn(): CompletableFuture<INode>

    /**
     * Returns the termination future
     * The termination future will be completed when the process was stopped
     * This methods always returns the same [CompletableFuture]
     */
    fun terminationFuture(): CompletableFuture<Void>

    /**
     * Returns the started future
     * The started future will be completed when the process was started
     * This methods always returns the same [CompletableFuture]
     */
    fun startedFuture(): CompletableFuture<Void>

    /**
     * Creates a request to stop this process
     */
    fun createStopRequest(): IProcessStopRequest



}