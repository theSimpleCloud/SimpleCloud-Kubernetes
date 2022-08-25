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
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.api.service.StaticProcessTemplateService
import app.simplecloud.simplecloud.api.template.static.StaticProcessTemplate

class NodeStaticOnlineProcessesHandler(
    private val staticTemplateService: StaticProcessTemplateService,
    private val processService: CloudProcessService,
) {

    suspend fun handleProcesses() {
        val templates = this.staticTemplateService.findAll().await()
        templates.forEach {
            handleSingleTemplate(it)
        }
    }

    private suspend fun handleSingleTemplate(template: StaticProcessTemplate) {
        if (template.isActive()) {
            ensureProcessOnline(template)
        } else {
            ensureProcessOffline(template)
        }
    }

    private suspend fun ensureProcessOffline(template: StaticProcessTemplate) {
        if (!doesProcessExist(template))
            return
        val process = getProcessByTemplate(template)
        process.createShutdownRequest().submit().await()
    }

    private suspend fun ensureProcessOnline(template: StaticProcessTemplate) {
        if (doesProcessExist(template))
            return
        this.processService.createStartRequest(template).submit().await()
    }

    private suspend fun doesProcessExist(template: StaticProcessTemplate): Boolean {
        return try {
            this.processService.findByName(template.getName()).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun getProcessByTemplate(template: StaticProcessTemplate): CloudProcess {
        return this.processService.findByName(template.getName()).await()
    }

}