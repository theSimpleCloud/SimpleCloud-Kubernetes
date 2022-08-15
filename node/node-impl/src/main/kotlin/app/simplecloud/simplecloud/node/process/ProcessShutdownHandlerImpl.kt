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

package app.simplecloud.simplecloud.node.process

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.internal.request.process.InternalProcessUpdateRequest
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.process.state.ProcessState
import app.simplecloud.simplecloud.kubernetes.api.pod.KubePodService
import org.apache.logging.log4j.LogManager

/**
 * Date: 27.03.22
 * Time: 18:49
 * @author Frederick Baier
 *
 */
class ProcessShutdownHandlerImpl(
    private val process: CloudProcess,
    private val podService: KubePodService,
) : ProcessShutdownHandler {

    override suspend fun shutdownProcess() {
        logger.info("Stopping Process {}", this.process.getName())
        updateStateToClosed()
        val container = podService.getPod(this.process.getName().lowercase())
        container.shutdown()
    }

    private suspend fun updateStateToClosed() {
        val updateRequest = this.process.createUpdateRequest()
        updateRequest as InternalProcessUpdateRequest
        updateRequest.setState(ProcessState.CLOSED)
        updateRequest.submit().await()
    }

    companion object {
        private val logger = LogManager.getLogger(ProcessShutdownHandlerImpl::class.java)
    }

}