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
import app.simplecloud.simplecloud.api.image.Image
import app.simplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessService
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.request.process.ProcessStartRequest
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 04.04.2021
 * Time: 13:31
 * @author Frederick Baier
 */
class ProcessStartRequestImpl(
    private val internalService: InternalCloudProcessService,
    private val processGroup: CloudProcessGroup
) : ProcessStartRequest {

    @Volatile
    private var maxPlayers: Int = this.processGroup.getMaxPlayers()

    @Volatile
    private var maxMemory: Int = this.processGroup.getMaxMemory()

    @Volatile
    private var processNumber: Int = -1

    @Volatile
    private var image: Image = this.processGroup.getImage()

    override fun getProcessGroup(): CloudProcessGroup {
        return this.processGroup
    }

    override fun setMaxPlayers(maxPlayers: Int): ProcessStartRequest {
        require(maxPlayers >= -1) { "Max Players must be greater than -2" }
        this.maxPlayers = maxPlayers
        return this
    }

    override fun setMaxMemory(memory: Int): ProcessStartRequest {
        require(memory >= 256) { "Max Memory must be 256 or higher" }
        this.maxMemory = memory
        return this
    }

    override fun setImage(image: Image): ProcessStartRequest {
        this.image = image
        return this
    }

    override fun setProcessNumber(number: Int): ProcessStartRequest {
        require(number > 0) { "The port must be positive" }
        this.processNumber = number
        return this
    }

    override fun submit(): CompletableFuture<CloudProcess> {
        return startProcess()
    }

    private fun startProcess(): CompletableFuture<CloudProcess> = CloudScope.future {
        val startConfiguration = ProcessStartConfiguration(
            processGroup.getName(),
            processNumber,
            image.getName(),
            maxMemory,
            maxPlayers,
            processGroup.isStatic(),
            processGroup.getProcessGroupType()
        )
        return@future internalService.startNewProcessInternal(startConfiguration)
    }
}