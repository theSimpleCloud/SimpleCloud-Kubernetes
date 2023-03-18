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

package app.simplecloud.simplecloud.node.resource.process

import app.simplecloud.simplecloud.api.future.isCompletedNormally
import app.simplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.api.service.StaticProcessTemplateService
import app.simplecloud.simplecloud.api.template.ProcessTemplate
import app.simplecloud.simplecloud.node.process.InternalProcessStartHandler
import app.simplecloud.simplecloud.node.process.ProcessStarter

/**
 * Date: 16.03.23
 * Time: 10:06
 * @author Frederick Baier
 *
 */
class V1Beta1ProcessCreateHandler(
    private val processName: String,
    private val spec: V1Beta1CloudProcessSpec,
    private val groupService: CloudProcessGroupService,
    private val staticService: StaticProcessTemplateService,
    private val processService: CloudProcessService,
    private val processStarterFactory: ProcessStarter.Factory,
) {

    private val processTemplateName = getProcessTemplateName()
    private val processTemplate = getProcessTemplate()

    suspend fun handleCreate(): V1Beta1CloudProcessSpec {
        val configuration = ProcessStartConfiguration(
            this.processTemplate.getName(),
            getProcessNumber(),
            this.spec.imageName ?: this.processTemplate.getImage().getName(),
            determineMaxMemory(),
            determineMaxPlayers(),
            this.processTemplate.getProcessTemplateType(),
            this.processTemplate.isStatic()
        )
        InternalProcessStartHandler(configuration, this.processService, this.processStarterFactory)
            .startProcess()
        return V1Beta1CloudProcessSpec(configuration.maxMemory, configuration.maxPlayers, configuration.imageName)
    }

    private fun getProcessTemplateName(): String {
        if (!this.processName.contains("-"))
            return this.processName

        val numberOfGroupProcess = this.processName.split("-").last()
        if (numberOfGroupProcess.toIntOrNull() == null) {
            return this.processName
        }
        return this.processName.split("-").dropLast(1).joinToString("-")
    }

    private fun getProcessNumber(): Int {
        if (this.processTemplateName == this.processName)
            return -1
        return this.processName.split("-").last().toInt()
    }

    private fun getProcessTemplate(): ProcessTemplate {
        val groupFuture = this.groupService.findByName(this.processTemplateName)
        val staticFuture = this.staticService.findByName(this.processTemplateName)
        kotlin.runCatching { groupFuture.join() }
        kotlin.runCatching { staticFuture.join() }
        if (groupFuture.isCompletedNormally) {
            return groupFuture.get()
        }
        if (staticFuture.isCompletedNormally) {
            return staticFuture.get()
        }
        throw IllegalStateException("Unknown ProcessTemplate ${this.processTemplateName}")
    }

    private fun determineMaxMemory(): Int {
        val maxMemory = this.spec.maxMemory ?: this.processTemplate.getMaxMemory()
        if (maxMemory < 256) {
            throw IllegalArgumentException("Memory cannot be lower than 256")
        }
        return maxMemory
    }

    private fun determineMaxPlayers(): Int {
        val maxPlayers = this.spec.maxPlayers ?: this.processTemplate.getMaxPlayers()
        if (maxPlayers < -1) {
            throw IllegalArgumentException("Max Players cannot be lower than -1")
        }
        return maxPlayers
    }


}