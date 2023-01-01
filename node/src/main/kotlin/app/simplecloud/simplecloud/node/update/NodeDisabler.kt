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

package app.simplecloud.simplecloud.node.update

import app.simplecloud.simplecloud.module.api.internal.service.InternalNodeCloudAPI

/**
 * Date: 01.01.23
 * Time: 16:31
 * @author Frederick Baier
 *
 */
class NodeDisabler(
    private val cloudAPI: InternalNodeCloudAPI,
) {

    fun disableNodes() {
        if (this.cloudAPI.isDisabledMode().get()) {
            throw AlreadyDisabledException()
        }
        this.cloudAPI.setDisabledMode(true)
        stopAllProcesses()
        stopAllFtpServers()
    }

    private fun stopAllFtpServers() {
        val ftpService = this.cloudAPI.getFtpService()
        val ftpServersFuture = ftpService.findAll()
        ftpServersFuture.thenAccept { list -> list.forEach { it.createStopRequest().submit() } }
    }

    private fun stopAllProcesses() {
        val processService = this.cloudAPI.getProcessService()
        val processesFuture = processService.findAll()
        processesFuture.thenAccept { list -> list.forEach { it.createShutdownRequest().submit() } }
    }

    class AlreadyDisabledException() : Exception("Already disabled")

}