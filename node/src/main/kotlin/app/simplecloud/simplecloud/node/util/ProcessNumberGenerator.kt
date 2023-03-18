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

package app.simplecloud.simplecloud.node.util

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.service.CloudProcessService

/**
 * Date: 17.03.23
 * Time: 09:21
 * @author Frederick Baier
 *
 */
class ProcessNumberGenerator(
    private val processService: CloudProcessService,
    private val processTemplateName: String,
) {

    suspend fun validateProcessNumber(processNumber: Int) {
        if (isProcessNumberInUse(processNumber)) {
            throw IllegalArgumentException("Process number $processNumber is already in use")
        }
    }

    suspend fun generateNewProcessNumber(): Int {
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
        return this.processTemplateName + "-" + number
    }

}