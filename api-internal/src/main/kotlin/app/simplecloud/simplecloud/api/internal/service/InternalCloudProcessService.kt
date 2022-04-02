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

package app.simplecloud.simplecloud.api.internal.service

import app.simplecloud.simplecloud.api.internal.configutation.ProcessExecuteCommandConfiguration
import app.simplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.process.CloudProcessConfiguration
import app.simplecloud.simplecloud.api.service.CloudProcessService

/**
 * Created by IntelliJ IDEA.
 * Date: 04.04.2021
 * Time: 19:58
 * @author Frederick Baier
 */
interface InternalCloudProcessService : CloudProcessService {

    /**
     * Starts a new process with the specified [configuration]
     * @return the newly registered process
     */
    suspend fun startNewProcessInternal(configuration: ProcessStartConfiguration): CloudProcess

    /**
     * Executes a command on the specified process
     */
    suspend fun executeCommandInternal(configuration: ProcessExecuteCommandConfiguration)

    /**
     * Shuts the [process] down
     * @return the [CloudProcess.terminationFuture] of the process
     */
    suspend fun shutdownProcessInternal(process: CloudProcess)

    suspend fun updateProcessInternal(configuration: CloudProcessConfiguration)

}