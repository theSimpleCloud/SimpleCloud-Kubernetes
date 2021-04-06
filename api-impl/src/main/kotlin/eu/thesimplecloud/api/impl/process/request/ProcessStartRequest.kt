package eu.thesimplecloud.api.impl.process.request

import eu.thesimplecloud.api.impl.future.flatten
import eu.thesimplecloud.api.internal.InternalCloudAPI
import eu.thesimplecloud.api.internal.configutation.ProcessStartConfiguration
import eu.thesimplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.api.process.ICloudProcess
import eu.thesimplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.api.process.request.IProcessStartRequest
import eu.thesimplecloud.api.template.ITemplate
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 04.04.2021
 * Time: 13:31
 * @author Frederick Baier
 */
class ProcessStartRequest(
    private val processGroup: ICloudProcessGroup
) : IProcessStartRequest {

    @Volatile
    private var maxPlayers: Int = this.processGroup.getMaxPlayers()

    @Volatile
    private var maxMemory: Int = this.processGroup.getMaxMemory()

    @Volatile
    private var processNumber: Int = -1

    @Volatile
    private var templateFuture: CompletableFuture<ITemplate> = this.processGroup.getTemplate()

    @Volatile
    private var jvmArgumentsFuture: CompletableFuture<IJVMArguments> = this.processGroup.getJvmArguments()

    override fun getProcessGroup(): ICloudProcessGroup {
        return this.processGroup
    }

    override fun setMaxPlayers(maxPlayers: Int): IProcessStartRequest {
        this.maxPlayers = maxPlayers
        return this
    }

    override fun setMaxMemory(memory: Int): IProcessStartRequest {
        this.maxMemory = memory
        return this
    }

    override fun setTemplate(template: ITemplate): IProcessStartRequest {
        this.templateFuture = CompletableFuture.completedFuture(template)
        return this
    }

    override fun setTemplate(templateFuture: CompletableFuture<ITemplate>): IProcessStartRequest {
        this.templateFuture = templateFuture
        return this
    }

    override fun setProcessNumber(number: Int): IProcessStartRequest {
        require(number > 0) { "The port must be positive" }
        this.processNumber = number
        return this
    }

    override fun setJvmArguments(arguments: IJVMArguments): IProcessStartRequest {
        this.jvmArgumentsFuture = CompletableFuture.completedFuture(arguments)
        return this
    }

    override fun setJvmArguments(argumentsFuture: CompletableFuture<IJVMArguments>): IProcessStartRequest {
        this.jvmArgumentsFuture = argumentsFuture
        return this
    }

    override fun submit(): CompletableFuture<ICloudProcess> {
        return this.jvmArgumentsFuture.thenCombine(this.templateFuture) { arguments, template ->
            return@thenCombine startProcess(arguments, template)
        }.flatten()
    }

    private fun startProcess(arguments: IJVMArguments, template: ITemplate): CompletableFuture<ICloudProcess> {
        val startConfiguration = ProcessStartConfiguration(
            this.processGroup.getName(), this.processNumber,
            template.getName(), this.maxPlayers,
            this.maxPlayers,
            arguments.getName()
        )
        return InternalCloudAPI.instance.getProcessService().startNewProcess(startConfiguration)
    }
}