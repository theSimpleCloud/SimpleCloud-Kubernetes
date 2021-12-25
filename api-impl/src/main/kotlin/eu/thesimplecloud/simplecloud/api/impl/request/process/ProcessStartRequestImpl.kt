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

package eu.thesimplecloud.simplecloud.api.impl.request.process

import com.ea.async.Async.await
import eu.thesimplecloud.simplecloud.api.image.Image
import eu.thesimplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import eu.thesimplecloud.simplecloud.api.internal.service.InternalCloudProcessService
import eu.thesimplecloud.simplecloud.api.jvmargs.JVMArguments
import eu.thesimplecloud.simplecloud.api.process.CloudProcess
import eu.thesimplecloud.simplecloud.api.process.group.CloudProcessGroup
import eu.thesimplecloud.simplecloud.api.request.process.ProcessStartRequest
import eu.thesimplecloud.simplecloud.api.utils.future.CloudCompletableFuture
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 04.04.2021
 * Time: 13:31
 * @author Frederick Baier
 */
class ProcessStartRequestImpl(
    private val internalService: InternalCloudProcessService,
    private val processGroup: CloudProcessGroup
) : ProcessStartRequest {

    @Volatile
    private var maxPlayers: Int = this.processGroup.getMaxPlayers()

    @Volatile
    private var maxMemory: Int = this.processGroup.getMaxMemory()

    @Volatile
    private var processNumber: Int = -1

    @Volatile
    private var image: Image = this.processGroup.getImage()

    @Volatile
    private var jvmArgumentsFuture: CompletableFuture<JVMArguments> = this.processGroup.getJvmArguments()

    override fun getProcessGroup(): CloudProcessGroup {
        return this.processGroup
    }

    override fun setMaxPlayers(maxPlayers: Int): ProcessStartRequest {
        this.maxPlayers = maxPlayers
        return this
    }

    override fun setMaxMemory(memory: Int): ProcessStartRequest {
        this.maxMemory = memory
        return this
    }

    override fun setImage(image: Image): ProcessStartRequest {
        this.image = image
        return this
    }

    override fun setProcessNumber(number: Int): ProcessStartRequest {
        require(number > 0) { "The port must be positive" }
        this.processNumber = number
        return this
    }

    override fun setJvmArguments(arguments: JVMArguments): ProcessStartRequest {
        this.jvmArgumentsFuture = CloudCompletableFuture.completedFuture(arguments)
        return this
    }

    override fun setJvmArguments(argumentsFuture: CompletableFuture<JVMArguments>): ProcessStartRequest {
        this.jvmArgumentsFuture = argumentsFuture
        return this
    }

    override fun submit(): CompletableFuture<CloudProcess> {
        val jvmArguments = try {
            await(this.jvmArgumentsFuture)
        } catch (ex: Exception) {
            null
        }
        return startProcess(jvmArguments)
    }

    private fun startProcess(arguments: JVMArguments?): CompletableFuture<CloudProcess> {
        val startConfiguration = ProcessStartConfiguration(
            this.processGroup.getName(),
            this.processNumber,
            this.image.getName(),
            this.maxMemory,
            this.maxPlayers,
            arguments?.getName()
        )
        return this.internalService.startNewProcessInternal(startConfiguration)
    }
}