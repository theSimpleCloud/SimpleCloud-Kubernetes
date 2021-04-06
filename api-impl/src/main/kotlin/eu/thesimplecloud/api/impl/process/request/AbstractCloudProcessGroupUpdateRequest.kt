package eu.thesimplecloud.api.impl.process.request

import eu.thesimplecloud.api.impl.future.combineToVoidFuture
import eu.thesimplecloud.api.impl.future.flatten
import eu.thesimplecloud.api.impl.future.getNow
import eu.thesimplecloud.api.impl.future.getNowOrNull
import eu.thesimplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.api.process.group.update.ICloudProcessGroupUpdateRequest
import eu.thesimplecloud.api.process.onlineonfiguration.IProcessesOnlineCountConfiguration
import eu.thesimplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.api.template.ITemplate
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 04.04.2021
 * Time: 21:35
 * @author Frederick Baier
 */
abstract class AbstractCloudProcessGroupUpdateRequest(
    private val processGroup: ICloudProcessGroup
) : ICloudProcessGroupUpdateRequest {

    @Volatile
    protected var maxMemory: Int = this.processGroup.getMaxMemory()

    @Volatile
    protected var maxPlayers: Int = this.processGroup.getMaxPlayers()

    @Volatile
    protected var maintenance: Boolean = this.processGroup.isInMaintenance()

    @Volatile
    protected var minProcessCount: Int = this.processGroup.getMinimumOnlineProcessCount()

    @Volatile
    protected var maxProcessCount: Int = this.processGroup.getMaximumOnlineProcessCount()

    @Volatile
    protected var joinPermission: String? = this.processGroup.getJoinPermission()

    @Volatile
    protected var stateUpdating: Boolean = this.processGroup.isStateUpdatingEnabled()

    @Volatile
    protected var startPriority: Int = this.processGroup.getStartPriority()

    @Volatile
    protected var versionFuture: CompletableFuture<IProcessVersion> = this.processGroup.getVersion()

    @Volatile
    protected var templateFuture: CompletableFuture<ITemplate> = this.processGroup.getTemplate()

    @Volatile
    protected var jvmArgumentsFuture: CompletableFuture<IJVMArguments> = this.processGroup.getJvmArguments()

    @Volatile
    protected var onlineCountConfigurationFuture: CompletableFuture<IProcessesOnlineCountConfiguration> = this.processGroup.getProcessOnlineCountConfiguration()

    override fun getProcessGroup(): ICloudProcessGroup {
        return this.processGroup
    }

    override fun setMaxMemory(memory: Int): ICloudProcessGroupUpdateRequest {
        this.maxMemory = memory
        return this
    }

    override fun setMaxPlayers(players: Int): ICloudProcessGroupUpdateRequest {
        this.maxPlayers = players
        return this
    }

    override fun setVersion(version: IProcessVersion): ICloudProcessGroupUpdateRequest {
        this.versionFuture = CompletableFuture.completedFuture(version)
        return this
    }

    override fun setVersion(versionFuture: CompletableFuture<IProcessVersion>): ICloudProcessGroupUpdateRequest {
        this.versionFuture = versionFuture
        return this
    }

    override fun setTemplate(template: ITemplate): ICloudProcessGroupUpdateRequest {
        this.templateFuture = CompletableFuture.completedFuture(template)
        return this
    }

    override fun setTemplate(templateFuture: CompletableFuture<ITemplate>): ICloudProcessGroupUpdateRequest {
        this.templateFuture = templateFuture
        return this
    }

    override fun setJvmArguments(jvmArguments: IJVMArguments): ICloudProcessGroupUpdateRequest {
        this.jvmArgumentsFuture = CompletableFuture.completedFuture(jvmArguments)
        return this
    }

    override fun setJvmArguments(jvmArgumentsFuture: CompletableFuture<IJVMArguments>): ICloudProcessGroupUpdateRequest {
        this.jvmArgumentsFuture = jvmArgumentsFuture
        return this
    }

    override fun setOnlineCountConfiguration(onlineCountConfiguration: IProcessesOnlineCountConfiguration): ICloudProcessGroupUpdateRequest {
        this.onlineCountConfigurationFuture = CompletableFuture.completedFuture(onlineCountConfiguration)
        return this
    }

    override fun setOnlineCountConfiguration(onlineCountConfigurationFuture: CompletableFuture<IProcessesOnlineCountConfiguration>): ICloudProcessGroupUpdateRequest {
        this.onlineCountConfigurationFuture = onlineCountConfigurationFuture
        return this
    }

    override fun setMaintenance(maintenance: Boolean): ICloudProcessGroupUpdateRequest {
        this.maintenance = maintenance
        return this
    }

    override fun setMinimumOnlineProcessCount(minCount: Int): ICloudProcessGroupUpdateRequest {
        this.minProcessCount = minCount
        return this
    }

    override fun setMaximumOnlineProcessCount(maxCount: Int): ICloudProcessGroupUpdateRequest {
        this.maxProcessCount = maxCount
        return this
    }

    override fun setJoinPermission(permission: String?): ICloudProcessGroupUpdateRequest {
        this.joinPermission = permission
        return this
    }

    override fun setStateUpdating(stateUpdating: Boolean): ICloudProcessGroupUpdateRequest {
        this.stateUpdating = stateUpdating
        return this
    }

    override fun setStartPriority(priority: Int): ICloudProcessGroupUpdateRequest {
        this.startPriority = priority
        return this
    }

    override fun submit(): CompletableFuture<ICloudProcessGroup> {
        val voidFuture = listOf(this.versionFuture, this.templateFuture, this.jvmArgumentsFuture, this.onlineCountConfigurationFuture).combineToVoidFuture()
        return voidFuture.thenApply {
            return@thenApply submit0(
                this.versionFuture.getNow(),
                this.templateFuture.getNow(),
                this.jvmArgumentsFuture.getNowOrNull(),
                this.onlineCountConfigurationFuture.getNow()
            )
        }.flatten()
    }

    abstract fun submit0(
        version: IProcessVersion,
        template: ITemplate,
        jvmArguments: IJVMArguments?,
        onlineCountConfiguration: IProcessesOnlineCountConfiguration
    ): CompletableFuture<ICloudProcessGroup>


}