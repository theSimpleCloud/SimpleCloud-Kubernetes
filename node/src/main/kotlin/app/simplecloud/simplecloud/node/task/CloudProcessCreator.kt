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
import app.simplecloud.simplecloud.api.impl.process.factory.CloudProcessFactory
import app.simplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.process.CloudProcessConfiguration
import app.simplecloud.simplecloud.api.process.state.ProcessState
import app.simplecloud.simplecloud.api.service.CloudProcessService
import java.util.*

class CloudProcessCreator(
    private val startConfiguration: ProcessStartConfiguration,
    private val processService: CloudProcessService,
    private val factory: CloudProcessFactory
) {

    suspend fun createProcess(): CloudProcess {
        val processNumber = getProcessNumber()
        return this.factory.create(
            CloudProcessConfiguration(
                startConfiguration.groupName,
                UUID.randomUUID(),
                processNumber,
                ProcessState.PREPARED,
                true,
                startConfiguration.maxMemory,
                0,
                startConfiguration.maxPlayers,
                0,
                startConfiguration.isStatic,
                startConfiguration.groupType,
                startConfiguration.imageName,
                null
            ),
            processService
        )
    }

    private suspend fun getProcessNumber(): Int {
        if (!this.startConfiguration.isProcessNumberSet()) {
            return generateNewProcessNumber()
        }
        return validateProcessNumber()
    }

    private suspend fun validateProcessNumber(): Int {
        val processNumber = this.startConfiguration.processNumber
        if (isProcessNumberInUse(processNumber)) {
            throw IllegalArgumentException("Process number $processNumber is already in use")
        }
        return processNumber
    }

    private suspend fun generateNewProcessNumber(): Int {
        var number = 1
        while (isProcessNumberInUse(number)) {
            number++
        }
        return number
    }

    private suspend fun isProcessNumberInUse(number: Int): Boolean {
        return try {
            this.processService.findByName(getNewProcessName(number)).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getNewProcessName(number: Int): String {
        return this.startConfiguration.groupName + "-" + number
    }

}