/*
 * SimpleCloud is a software for administrating a minecraft server network.
 * Copyright (C) 2022 Frederick Baier & Philipp Eistrach
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package app.simplecloud.simplecloud.node.task

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.process.state.ProcessState
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.api.service.NodeProcessOnlineStrategyService
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
        if (count < 1) return
        val processes = this.group.getProcesses().await()
        val stoppableServices = processes.filter { it.getState() == ProcessState.ONLINE && it.isVisible() }
        stoppableServices.take(count).forEach { it.createShutdownRequest().submit() }
    }

    private suspend fun startServices(count: Int) {
        for (i in 0 until count) {
            this.processService.createStartRequest(this.group).submit().await()
        }
    }

    private suspend fun calculateExpectedOnlineCount(): Int {
        val config = this.nodeProcessOnlineStrategyService.findByProcessGroupName(this.group.getName()).await()
        return config.calculateOnlineCount(this.group)
    }

    private suspend fun calculateActualOnlineCount(): Int {
        val processes = this.group.getProcesses().await()
        return processes.count { isProcessAvailableJoinableOrBefore(it) }
    }

    private fun isProcessAvailableJoinableOrBefore(process: CloudProcess): Boolean {
        val state = process.getState()
        return state == ProcessState.PREPARED ||
                state == ProcessState.STARTING ||
                (state == ProcessState.ONLINE && process.isVisible())
    }


    companion object {
        private val logger = LogManager.getLogger(ProcessOnlineCountHandler::class.java)
    }

}