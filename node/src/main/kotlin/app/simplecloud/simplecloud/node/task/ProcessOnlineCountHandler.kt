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

package app.simplecloud.simplecloud.node.task

import app.simplecloud.simplecloud.api.future.completedFuture
import app.simplecloud.simplecloud.api.future.unitFuture
import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.process.state.ProcessState
import app.simplecloud.simplecloud.api.service.CloudProcessService
import com.ea.async.Async.await
import org.apache.logging.log4j.LogManager
import java.util.concurrent.CompletableFuture

class ProcessOnlineCountHandler(
    private val group: CloudProcessGroup,
    private val processService: CloudProcessService
) {

    fun handle(): CompletableFuture<Unit> {
        logger.info("Checking online count for {}", group.getName())
        val expectedCount = await(calculateExpectedOnlineCount())
        val actualCount = await(calculateActualOnlineCount())
        if (expectedCount > actualCount) {
            startServices(expectedCount - actualCount)
            return unitFuture()
        }
        stopServices(actualCount - expectedCount)
        return unitFuture()
    }

    private fun stopServices(count: Int) {

    }

    private fun startServices(count: Int) {
        for (i in 0 until count) {
            await(this.processService.createProcessStartRequest(this.group).submit())
        }
    }

    private fun calculateExpectedOnlineCount(): CompletableFuture<Int> {
        val onlineCountConfiguration = await(this.group.getProcessOnlineCountConfiguration())
        return completedFuture(onlineCountConfiguration.calculateOnlineCount(this.group))
    }

    private fun calculateActualOnlineCount(): CompletableFuture<Int> {
        val processes = await(this.group.getProcesses())
        val count = processes.count { isStateJoinableOrBefore(it.getState()) }
        return completedFuture(count)
    }

    private fun isStateJoinableOrBefore(processState: ProcessState): Boolean {
        return processState == ProcessState.PREPARED ||
                processState == ProcessState.STARTING ||
                processState == ProcessState.VISIBLE
    }


    companion object {
        private val logger = LogManager.getLogger(ProcessOnlineCountHandler::class.java)
    }

}