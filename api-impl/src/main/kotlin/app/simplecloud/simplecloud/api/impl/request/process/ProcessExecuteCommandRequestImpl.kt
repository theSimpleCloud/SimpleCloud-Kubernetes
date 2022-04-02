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

package app.simplecloud.simplecloud.api.impl.request.process

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.api.internal.configutation.ProcessExecuteCommandConfiguration
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessService
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.request.process.ProcessExecuteCommandRequest
import java.util.concurrent.CompletableFuture

/**
 * Date: 01.04.22
 * Time: 14:31
 * @author Frederick Baier
 *
 */
class ProcessExecuteCommandRequestImpl(
    private val command: String,
    private val process: CloudProcess,
    private val internalService: InternalCloudProcessService
) : ProcessExecuteCommandRequest {

    override fun submit(): CompletableFuture<Unit> = CloudScope.future {
        val configuration = ProcessExecuteCommandConfiguration(process.getName(), command)
        internalService.executeCommandInternal(configuration)
    }
}