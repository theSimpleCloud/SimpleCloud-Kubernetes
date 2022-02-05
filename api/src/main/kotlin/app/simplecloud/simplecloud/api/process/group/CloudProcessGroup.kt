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

package app.simplecloud.simplecloud.api.process.group

import app.simplecloud.simplecloud.api.image.Image
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import app.simplecloud.simplecloud.api.process.onlineonfiguration.ProcessesOnlineCountConfiguration
import app.simplecloud.simplecloud.api.request.group.ProcessGroupDeleteRequest
import app.simplecloud.simplecloud.api.request.group.update.CloudProcessGroupUpdateRequest
import app.simplecloud.simplecloud.api.utils.Identifiable
import app.simplecloud.simplecloud.api.utils.Nameable
import java.util.concurrent.CompletableFuture


interface CloudProcessGroup : Nameable, Identifiable<String> {

    fun getMaxMemory(): Int

    fun getMaxPlayers(): Int

    fun getOnlinePlayerCount(): Int

    fun isInMaintenance(): Boolean

    fun getImage(): Image

    fun getProcessOnlineCountConfigurationName(): String

    fun getProcessOnlineCountConfiguration(): CompletableFuture<ProcessesOnlineCountConfiguration>

    fun getProcessGroupType(): ProcessGroupType

    fun getJoinPermission(): String?

    fun isStatic(): Boolean

    fun isStateUpdatingEnabled(): Boolean

    fun getStartPriority(): Int

    fun getProcesses(): CompletableFuture<List<CloudProcess>>

    fun toConfiguration(): AbstractCloudProcessGroupConfiguration

    fun createUpdateRequest(): CloudProcessGroupUpdateRequest

    fun createDeleteRequest(): ProcessGroupDeleteRequest

}