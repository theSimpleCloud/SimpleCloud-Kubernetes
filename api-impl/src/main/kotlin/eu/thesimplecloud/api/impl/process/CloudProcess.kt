package eu.thesimplecloud.api.impl.process

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.impl.future.exception.CompletedWithNullException
import eu.thesimplecloud.api.impl.process.request.ProcessStopRequest
import eu.thesimplecloud.api.impl.utils.AbstractNetworkComponent
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
    private val groupName: String,
    private val uniqueId: UUID,
    private val processNumber: Int,
    private val state: ProcessState,
    private val maxMemory: Int,
    private val usedMemory: Int,
    private val maxPlayers: Int,
    private val address: Address,
    private val static: Boolean,
    private val processGroupType: ProcessGroupType,
    private val versionName: String,
    private val templateName: String,
    private val nodeNameRunningOn: String,
    private val jvmArgumentsName: String?,
) : AbstractNetworkComponent(), ICloudProcess {

    override fun getGroup(): CompletableFuture<ICloudProcessGroup> {
        return CloudAPI.instance.getProcessGroupService().findByName(this.groupName)
    }

    override fun getProcessNumber(): Int {
        return this.processNumber
    }

    override fun getState(): ProcessState {
        return this.state
    }

    override fun getMaxMemory(): Int {
        return this.maxMemory
    }

    override fun getUsedMemory(): Int {
        return this.usedMemory
    }

    override fun getMaxPlayers(): Int {
        return this.maxPlayers
    }

    override fun getAddress(): Address {
        return this.address
    }

    override fun isStatic(): Boolean {
        return this.static
    }

    override fun getProcessType(): ProcessGroupType {
        return this.processGroupType
    }

    override fun getVersion(): CompletableFuture<IProcessVersion> {
        return CloudAPI.instance.getProcessVersionService().findByName(this.versionName)
    }

    override fun getTemplate(): CompletableFuture<ITemplate> {
        return CloudAPI.instance.getTemplateService().findByName(this.templateName)
    }

    override fun getJvmArguments(): CompletableFuture<IJVMArguments> {
        if (this.jvmArgumentsName == null) return CompletableFuture.failedFuture(CompletedWithNullException())
        return CloudAPI.instance.getJvmArgumentsService().findByName(this.jvmArgumentsName)
    }

    override fun getNodeRunningOn(): CompletableFuture<INode> {
        return CloudAPI.instance.getNodeService().findNodeByName(this.nodeNameRunningOn)
    }

    override fun terminationFuture(): CompletableFuture<Void> {
        TODO("Not yet implemented")
    }

    override fun startedFuture(): CompletableFuture<Void> {
        TODO("Not yet implemented")
    }

    override fun createStopRequest(): IProcessStopRequest {
        return ProcessStopRequest(this)
    }

    override fun getUniqueId(): UUID {
        return this.uniqueId
    }

    override fun getName(): String {
        return this.groupName + "-" + getProcessNumber()
    }

    override fun getIdentifier(): String {
        return getName()
    }


}