/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.simplecloud.api.impl.process.request

import eu.thesimplecloud.simplecloud.api.impl.future.flatten
import eu.thesimplecloud.simplecloud.api.internal.InternalCloudAPI
import eu.thesimplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import eu.thesimplecloud.simplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.simplecloud.api.process.ICloudProcess
import eu.thesimplecloud.simplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.simplecloud.api.process.request.IProcessStartRequest
import eu.thesimplecloud.simplecloud.api.template.ITemplate
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