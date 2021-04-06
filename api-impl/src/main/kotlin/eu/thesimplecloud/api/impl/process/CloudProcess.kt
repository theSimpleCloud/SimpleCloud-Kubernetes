package eu.thesimplecloud.api.impl.process

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.api.node.INode
import eu.thesimplecloud.api.process.ICloudProcess
import eu.thesimplecloud.api.process.ProcessGroupType
import eu.thesimplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.api.process.request.IProcessStopRequest
import eu.thesimplecloud.api.process.state.ProcessState
import eu.thesimplecloud.api.template.ITemplate
import eu.thesimplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.api.utils.Address
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 27.03.2021
 * Time: 09:14
 * @author Frederick Baier
 */
class CloudProcess(
    private val groupName: String
) : ICloudProcess {

    override fun getGroup(): CompletableFuture<ICloudProcessGroup> {
        return CloudAPI.instance.getProcessGroupService().findByName(groupName)
    }

    override fun getNodeRunningOn(): CompletableFuture<INode> {
        TODO("Not yet implemented")
    }

    override fun getProcessNumber(): Int {
        TODO("Not yet implemented")
    }

    override fun getState(): ProcessState {
        TODO("Not yet implemented")
    }

    override fun getVersion(): CompletableFuture<IProcessVersion> {
        TODO("Not yet implemented")
    }

    override fun getTemplate(): CompletableFuture<ITemplate> {
        TODO("Not yet implemented")
    }

    override fun getJvmArguments(): CompletableFuture<IJVMArguments> {
        TODO("Not yet implemented")
    }

    override fun getMaxMemory(): Int {
        TODO("Not yet implemented")
    }

    override fun getUsedMemory(): Int {
        TODO("Not yet implemented")
    }

    override fun getMaxPlayers(): Int {
        TODO("Not yet implemented")
    }

    override fun getAddress(): Address {
        TODO("Not yet implemented")
    }

    override fun isStatic(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getProcessType(): ProcessGroupType {
        TODO("Not yet implemented")
    }

    override fun terminationFuture(): CompletableFuture<Void> {
        TODO("Not yet implemented")
    }

    override fun startedFuture(): CompletableFuture<Void> {
        TODO("Not yet implemented")
    }

    override fun createStopRequest(): IProcessStopRequest {
        TODO("Not yet implemented")
    }

    override fun getUniqueId(): UUID {
        TODO("Not yet implemented")
    }

    override fun getName(): String {
        TODO("Not yet implemented")
    }
}