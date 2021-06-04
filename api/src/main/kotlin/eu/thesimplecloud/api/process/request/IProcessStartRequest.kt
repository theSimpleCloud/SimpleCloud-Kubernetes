package eu.thesimplecloud.api.process.request

import eu.thesimplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.api.process.ICloudProcess
import eu.thesimplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.api.template.ITemplate
import eu.thesimplecloud.api.utils.IRequest
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 16.03.2021
 * Time: 20:09
 * @author Frederick Baier
 *
 * Used to configure a process before starting it
 *
 */
interface IProcessStartRequest : IRequest<ICloudProcess> {

    /**
     * Returns the process group this request will start a process of
     */
    fun getProcessGroup(): ICloudProcessGroup

    /**
     * Sets the max players for the new process
     * @return this
     */
    fun setMaxPlayers(maxPlayers: Int): IProcessStartRequest

    /**
     * Sets the max memory for the new process
     * @return this
     */
    fun setMaxMemory(memory: Int): IProcessStartRequest

    /**
     * Sets the template for the new process
     * @return this
     */
    fun setTemplate(template: ITemplate): IProcessStartRequest

    /**
     * Sets the template for the new process
     * @return this
     */
    fun setTemplate(templateFuture: CompletableFuture<ITemplate>): IProcessStartRequest

    /**
     * Sets the number of the new process
     * e.g: Lobby-2 -> 2 is the procoess number
     * @return this
     */
    fun setProcessNumber(number: Int): IProcessStartRequest

    /**
     * Sets the jvm arguments for the process to start with
     * @return this
     */
    fun setJvmArguments(arguments: IJVMArguments): IProcessStartRequest

    /**
     * Sets the jvm arguments for the process to start with
     * @return this
     */
    fun setJvmArguments(argumentsFuture: CompletableFuture<IJVMArguments>): IProcessStartRequest

}