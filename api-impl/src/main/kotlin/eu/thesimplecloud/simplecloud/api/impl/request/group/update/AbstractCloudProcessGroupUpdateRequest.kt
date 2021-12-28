/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.simplecloud.api.impl.request.group.update

import com.ea.async.Async.await
import eu.thesimplecloud.simplecloud.api.image.Image
import eu.thesimplecloud.simplecloud.api.process.group.CloudProcessGroup
import eu.thesimplecloud.simplecloud.api.process.onlineonfiguration.ProcessesOnlineCountConfiguration
import eu.thesimplecloud.simplecloud.api.request.group.update.CloudProcessGroupUpdateRequest
import eu.thesimplecloud.simplecloud.api.utils.future.CloudCompletableFuture
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 04.04.2021
 * Time: 21:35
 * @author Frederick Baier
 */
abstract class AbstractCloudProcessGroupUpdateRequest(
    private val processGroup: CloudProcessGroup
) : CloudProcessGroupUpdateRequest {

    @Volatile
    protected var maxMemory: Int = this.processGroup.getMaxMemory()

    @Volatile
    protected var maxPlayers: Int = this.processGroup.getMaxPlayers()

    @Volatile
    protected var maintenance: Boolean = this.processGroup.isInMaintenance()

    @Volatile
    protected var minProcessCount: Int = this.processGroup.getMinimumOnlineProcessCount()

    @Volatile
    protected var maxProcessCount: Int = this.processGroup.getMaximumOnlineProcessCount()

    @Volatile
    protected var joinPermission: String? = this.processGroup.getJoinPermission()

    @Volatile
    protected var stateUpdating: Boolean = this.processGroup.isStateUpdatingEnabled()

    @Volatile
    protected var startPriority: Int = this.processGroup.getStartPriority()

    @Volatile
    protected var image: Image? = runCatching { this.processGroup.getImage() }.getOrNull()

    @Volatile
    protected var onlineCountConfigurationFuture: CompletableFuture<ProcessesOnlineCountConfiguration> =
        this.processGroup.getProcessOnlineCountConfiguration()

    override fun getProcessGroup(): CloudProcessGroup {
        return this.processGroup
    }

    override fun setMaxMemory(memory: Int): CloudProcessGroupUpdateRequest {
        this.maxMemory = memory
        return this
    }

    override fun setMaxPlayers(players: Int): CloudProcessGroupUpdateRequest {
        this.maxPlayers = players
        return this
    }

    override fun setImage(image: Image?): CloudProcessGroupUpdateRequest {
        this.image = image
        return this
    }

    override fun setOnlineCountConfiguration(onlineCountConfiguration: ProcessesOnlineCountConfiguration): CloudProcessGroupUpdateRequest {
        this.onlineCountConfigurationFuture = CloudCompletableFuture.completedFuture(onlineCountConfiguration)
        return this
    }

    override fun setOnlineCountConfiguration(onlineCountConfigurationFuture: CompletableFuture<ProcessesOnlineCountConfiguration>): CloudProcessGroupUpdateRequest {
        this.onlineCountConfigurationFuture = onlineCountConfigurationFuture
        return this
    }

    override fun setMaintenance(maintenance: Boolean): CloudProcessGroupUpdateRequest {
        this.maintenance = maintenance
        return this
    }

    override fun setMinimumOnlineProcessCount(minCount: Int): CloudProcessGroupUpdateRequest {
        this.minProcessCount = minCount
        return this
    }

    override fun setMaximumOnlineProcessCount(maxCount: Int): CloudProcessGroupUpdateRequest {
        this.maxProcessCount = maxCount
        return this
    }

    override fun setJoinPermission(permission: String?): CloudProcessGroupUpdateRequest {
        this.joinPermission = permission
        return this
    }

    override fun setStateUpdating(stateUpdating: Boolean): CloudProcessGroupUpdateRequest {
        this.stateUpdating = stateUpdating
        return this
    }

    override fun setStartPriority(priority: Int): CloudProcessGroupUpdateRequest {
        this.startPriority = priority
        return this
    }

    override fun submit(): CompletableFuture<CloudProcessGroup> {
        val onlineCountConfiguration = await(this.onlineCountConfigurationFuture)
        return submit0(
            this.image,
            onlineCountConfiguration
        )
    }

    abstract fun submit0(
        image: Image?,
        onlineCountConfiguration: ProcessesOnlineCountConfiguration
    ): CompletableFuture<CloudProcessGroup>


}