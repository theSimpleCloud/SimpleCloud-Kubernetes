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

package app.simplecloud.simplecloud.node.process.unregister

import app.simplecloud.simplecloud.api.CloudAPI
import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedCloudProcessRepository
import app.simplecloud.simplecloud.api.internal.request.process.InternalProcessUpdateRequest
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.process.state.ProcessState
import app.simplecloud.simplecloud.kubernetes.api.KubeAPI
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.ResourceRequestHandler
import org.apache.logging.log4j.LogManager

/**
 * Date: 24.08.22
 * Time: 10:45
 * @author Frederick Baier
 *
 */
class ProcessUnregisterExecutor(
    private val kubeAPI: KubeAPI,
    private val cloudAPI: CloudAPI,
    private val distributedCloudProcessRepository: DistributedCloudProcessRepository,
    private val requestHandler: ResourceRequestHandler,
) {

    suspend fun compareProcessesWithKubeAndUnregister() {
        val processes = this.cloudAPI.getProcessService().findAll().await()
        val filteredProcesses = processes.filter { it.getState() != ProcessState.PREPARED }
        for (process in filteredProcesses) {
            unregisterProcessIfNoLongerRunning(process)
        }
    }

    private suspend fun unregisterProcessIfNoLongerRunning(process: CloudProcess) {
        if (doesContainerExist(process)) {
            stopContainerIfInactive(process)
        } else {
            deleteProcessFromCluster(process)
        }
    }

    private fun stopContainerIfInactive(process: CloudProcess) {
        val pod = this.kubeAPI.getPodService().getPod(process.getName().lowercase())
        if (!pod.isActive()) {
            pod.delete()
        }
    }

    private suspend fun deleteProcessFromCluster(process: CloudProcess) {
        updateStateToClosed(process)
        deleteProcessInCluster(process)
        deleteProcessInDatabase(process)
        logger.info("Unregistered process {}", process.getName())
    }

    private fun deleteProcessInDatabase(process: CloudProcess) {
        this.requestHandler.handleDelete("core", "CloudProcess", "v1beta1", process.getName())
    }

    private suspend fun updateStateToClosed(process: CloudProcess) {
        val updateRequest = process.createUpdateRequest()
        updateRequest as InternalProcessUpdateRequest
        updateRequest.setState(ProcessState.CLOSED)
        updateRequest.submit().await()
    }

    private fun doesContainerExist(process: CloudProcess): Boolean {
        return try {
            this.kubeAPI.getPodService().getPod(process.getName().lowercase())
            true
        } catch (e: NoSuchElementException) {
            false
        }
    }

    private suspend fun deleteProcessInCluster(process: CloudProcess) {
        this.distributedCloudProcessRepository.remove(process.getName()).await()
    }

    companion object {
        private val logger =
            LogManager.getLogger(ProcessUnregisterExecutor::class.java)
    }

}