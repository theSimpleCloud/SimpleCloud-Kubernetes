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

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.process.state.ProcessState
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.node.onlinestrategy.NodeProcessOnlineStrategyService
import org.apache.logging.log4j.LogManager

class ProcessOnlineCountHandler(
    private val group: CloudProcessGroup,
    private val processService: CloudProcessService,
    private val nodeProcessOnlineStrategyService: NodeProcessOnlineStrategyService
) {

    suspend fun handle() {
        logger.info("Checking online count for {}", group.getName())
        val expectedCount = calculateExpectedOnlineCount()
        val actualCount = calculateActualOnlineCount()
        if (expectedCount > actualCount) {
            startServices(expectedCount - actualCount)
        }
        stopServices(actualCount - expectedCount)
    }

    private suspend fun stopServices(count: Int) {

    }

    private suspend fun startServices(count: Int) {
        for (i in 0 until count) {
            this.processService.createStartRequest(this.group).submit().await()
        }
    }

    private suspend fun calculateExpectedOnlineCount(): Int {
        val config = this.nodeProcessOnlineStrategyService.getByProcessGroupName(this.group.getName()).await()
        return config.calculateOnlineCount(this.group)
    }

    private suspend fun calculateActualOnlineCount(): Int {
        val processes = this.group.getProcesses().await()
        return processes.count { isStateJoinableOrBefore(it.getState()) }
    }

    private fun isStateJoinableOrBefore(processState: ProcessState): Boolean {
        return processState == ProcessState.PREPARED ||
                processState == ProcessState.STARTING ||
                processState == ProcessState.ONLINE
    }


    companion object {
        private val logger = LogManager.getLogger(ProcessOnlineCountHandler::class.java)
    }

}