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
import app.simplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.ResourceRequestHandler
import app.simplecloud.simplecloud.node.resource.process.V1Beta1CloudProcessSpec
import app.simplecloud.simplecloud.node.util.ProcessNumberGenerator

/**
 * Date: 17.03.23
 * Time: 09:38
 * @author Frederick Baier
 *
 */
class InternalProcessStartRequestSender(
    private val configuration: ProcessStartConfiguration,
    private val processService: CloudProcessService,
    private val requestHandler: ResourceRequestHandler,
) {

    suspend fun sendStartRequest(): CloudProcess {
        val processSpec = createProcessSpec()
        val processName = createProcessName()
        this.requestHandler.handleCreate(
            "core",
            "CloudProcess",
            "v1beta1",
            processName,
            processSpec
        )
        return this.processService.findByName(processName).await()
    }

    private suspend fun createProcessName(): String {
        if (!this.configuration.isStatic && !this.configuration.isProcessNumberSet()) {
            val newProcessNumber = ProcessNumberGenerator(
                this.processService,
                this.configuration.processTemplateName
            ).generateNewProcessNumber()
            return configuration.processTemplateName + "-" + newProcessNumber
        }
        return this.configuration.getNewProcessName()
    }

    private fun createProcessSpec(): V1Beta1CloudProcessSpec {
        return V1Beta1CloudProcessSpec(
            this.configuration.maxMemory,
            this.configuration.maxPlayers,
            this.configuration.imageName
        )
    }

}