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

package eu.thesimplecloud.simplecloud.node.task

import com.ea.async.Async.await
import eu.thesimplecloud.simplecloud.api.future.completedFuture
import eu.thesimplecloud.simplecloud.api.impl.process.factory.CloudProcessFactory
import eu.thesimplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import eu.thesimplecloud.simplecloud.api.process.CloudProcessConfiguration
import eu.thesimplecloud.simplecloud.api.process.CloudProcess
import eu.thesimplecloud.simplecloud.api.process.group.CloudProcessGroup
import eu.thesimplecloud.simplecloud.api.process.group.CloudProxyGroup
import eu.thesimplecloud.simplecloud.api.process.state.ProcessState
import eu.thesimplecloud.simplecloud.api.service.CloudProcessGroupService
import eu.thesimplecloud.simplecloud.api.service.CloudProcessService
import java.util.*
import java.util.concurrent.CompletableFuture

class CloudProcessCreationTask(
    private val startConfiguration: ProcessStartConfiguration,
    private val processService: CloudProcessService,
    private val groupService: CloudProcessGroupService,
    private val factory: CloudProcessFactory
) {

    fun run(): CompletableFuture<CloudProcess> {
        val processNumber = await(getProcessNumber())
        val group = await(this.groupService.findByName(this.startConfiguration.groupName))
        val process = this.factory.create(
            CloudProcessConfiguration(
                this.startConfiguration.groupName,
                UUID.randomUUID(),
                processNumber,
                ProcessState.PREPARED,
                this.startConfiguration.maxMemory,
                0,
                this.startConfiguration.maxPlayers,
                group.isStatic(),
                group.getProcessGroupType(),
                this.startConfiguration.imageName,
                null
            )
        )
        return completedFuture(process)
    }

    private fun getProcessNumber(): CompletableFuture<Int> {
        if (!this.startConfiguration.isProcessNumberSet()) {
            return generateNewProcessNumber()
        }
        return validateProcessNumber()
    }

    private fun validateProcessNumber(): CompletableFuture<Int> {
        val processNumber = this.startConfiguration.processNumber
        if (await(isProcessNumberInUse(processNumber))) {
            throw IllegalArgumentException("Process number $processNumber is already in use")
        }
        return completedFuture(processNumber)
    }

    private fun generateNewProcessNumber(): CompletableFuture<Int> {
        var number = 1
        while (await(isProcessNumberInUse(number))) {
            number++
        }
        return completedFuture(number)
    }

    private fun isProcessNumberInUse(number: Int): CompletableFuture<Boolean> {
        return try {
            await(this.processService.findProcessByName(getNewProcessName(number)))
            completedFuture(true)
        } catch (e: Exception) {
            completedFuture(false)
        }
    }

    private fun getNewProcessName(number: Int): String {
        return this.startConfiguration.groupName + "-" + number
    }

}