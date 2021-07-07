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

package eu.thesimplecloud.simplecloud.api.request

import eu.thesimplecloud.simplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.simplecloud.api.process.ICloudProcess
import eu.thesimplecloud.simplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.simplecloud.api.template.ITemplate
import eu.thesimplecloud.simplecloud.api.utils.IRequest
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