package eu.thesimplecloud.api.impl.process.group

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.api.node.INode
import eu.thesimplecloud.api.process.ICloudProcess
import eu.thesimplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.api.process.onlineonfiguration.IProcessesOnlineCountConfiguration
import eu.thesimplecloud.api.process.request.IProcessGroupDeleteRequest
import eu.thesimplecloud.api.process.request.IProcessStartRequest
import eu.thesimplecloud.api.impl.process.request.ProcessGroupDeleteRequest
import eu.thesimplecloud.api.impl.process.request.ProcessStartRequest
import eu.thesimplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.api.template.ITemplate
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by IntelliJ IDEA.
 * Date: 27.03.2021
 * Time: 12:01
 * @author Frederick Baier
 */
abstract class AbstractCloudProcessGroup(
    private val name: String,
    private val maxMemory: Int,
    private val maxPlayers: Int,
    private val maintenance: Boolean,
    private val minimumProcessCount: Int,
    private val maximumProcessCount: Int,
    private val templateName: String,
    private val jvmArgumentName: String?,
    private val versionName: String,
    private val onlineCountConfigurationName: String,
    private val static: Boolean,
    private val stateUpdating: Boolean,
    private val startPriority: Int,
    private val joinPermission: String?,
) : ICloudProcessGroup {

    private val nodesAllowedToStartOn = CopyOnWriteArrayList<String>()

    @Volatile
    private var onlineCount = 0

    override fun getMaxMemory(): Int {
        return this.maxMemory
    }

    override fun getMaxPlayers(): Int {
        return this.maxPlayers
    }

    override fun getOnlinePlayerCount(): Int {
        return this.onlineCount
    }

    override fun isInMaintenance(): Boolean {
        return this.maintenance
    }

    override fun getTemplate(): CompletableFuture<ITemplate> {
        return CloudAPI.instance.getTemplateService().findByName(this.templateName)
    }

    override fun getVersion(): CompletableFuture<IProcessVersion> {
        return CloudAPI.instance.getProcessVersionService().findByName(this.versionName)
    }

    override fun getJvmArguments(): CompletableFuture<IJVMArguments> {
        if (this.jvmArgumentName == null) return CompletableFuture.failedFuture(NoSuchElementException())
        return CloudAPI.instance.getJvmArgumentsService().findByName(this.jvmArgumentName)
    }

    override fun getJoinPermission(): String? {
        return this.joinPermission
    }

    override fun getMinimumOnlineProcessCount(): Int {
        return this.minimumProcessCount
    }

    override fun getMaximumOnlineProcessCount(): Int {
        return this.maximumProcessCount
    }

    override fun isStatic(): Boolean {
        return this.static
    }

    override fun isStateUpdatingEnabled(): Boolean {
        return this.stateUpdating
    }

    override fun getStartPriority(): Int {
        return this.startPriority
    }

    override fun getNodesAllowedToStartServicesOn(): CompletableFuture<List<INode>> {
        return CloudAPI.instance.getNodeService().findNodesByName(*this.nodesAllowedToStartOn.toTypedArray())
    }

    override fun getProcesses(): CompletableFuture<List<ICloudProcess>> {
        return CloudAPI.instance.getProcessService().findProcessesByGroup(this)
    }

    override fun getProcessOnlineCountConfiguration(): CompletableFuture<IProcessesOnlineCountConfiguration> {
        return CloudAPI.instance.getProcessOnlineCountService().findProcessOnlineCountConfigurationByName(this.onlineCountConfigurationName)
    }

    override fun createProcessStartRequest(): IProcessStartRequest {
        return ProcessStartRequest(this)
    }

    override fun createDeleteRequest(): IProcessGroupDeleteRequest {
        return ProcessGroupDeleteRequest(this)
    }

    override fun getName(): String {
        return this.name
    }

    override fun getIdentifier(): String {
        return getName()
    }
}