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

package app.simplecloud.simplecloud.api.impl.process

import app.simplecloud.simplecloud.api.image.Image
import app.simplecloud.simplecloud.api.impl.image.ImageImpl
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.process.CloudProcessConfiguration
import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.process.group.ProcessGroupType
import app.simplecloud.simplecloud.api.process.state.ProcessState
import app.simplecloud.simplecloud.api.request.process.ProcessShutdownRequest
import app.simplecloud.simplecloud.api.request.process.ProcessUpdateRequest
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.api.utils.Address
import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 27.03.2021
 * Time: 09:14
 * @author Frederick Baier
 */
class CloudProcessImpl @Inject constructor(
    @Assisted private val configuration: CloudProcessConfiguration,
    private val processService: CloudProcessService,
    private val processGroupService: CloudProcessGroupService
) : CloudProcess {

    override fun getGroupName(): String {
        return this.configuration.groupName
    }

    override fun getGroup(): CompletableFuture<CloudProcessGroup> {
        return this.processGroupService.findByName(getGroupName())
    }

    override fun getProcessNumber(): Int {
        return this.configuration.processNumber
    }

    override fun getState(): ProcessState {
        return this.configuration.state
    }

    override fun isVisible(): Boolean {
        return this.configuration.visible
    }

    override fun getMaxMemory(): Int {
        return this.configuration.maxMemory
    }

    override fun getUsedMemory(): Int {
        return this.configuration.usedMemory
    }

    override fun getOnlinePlayers(): Int {
        return this.configuration.onlinePlayers
    }

    override fun getMaxPlayers(): Int {
        return this.configuration.maxPlayers
    }

    override fun isFull(): Boolean {
        return getOnlinePlayers() >= getMaxPlayers()
    }

    override fun getAddress(): Address {
        return Address(getName(), 25565)
    }

    override fun isStatic(): Boolean {
        return this.configuration.static
    }

    override fun getProcessType(): ProcessGroupType {
        return this.configuration.processGroupType
    }

    override fun getImage(): Image {
        return ImageImpl(this.configuration.imageName)
    }

    override fun terminationFuture(): CompletableFuture<Void> {
        TODO("Not yet implemented")
    }

    override fun startedFuture(): CompletableFuture<Void> {
        TODO("Not yet implemented")
    }

    override fun getUniqueId(): UUID {
        return this.configuration.uniqueId
    }

    override fun getName(): String {
        return getGroupName() + "-" + getProcessNumber()
    }

    override fun getIdentifier(): String {
        return getName()
    }

    override fun toConfiguration(): CloudProcessConfiguration {
        return this.configuration
    }

    override fun getIgniteId(): UUID {
        return this.configuration.igniteId ?: throw NullPointerException("Ignite id not set")
    }

    override fun createUpdateRequest(): ProcessUpdateRequest {
        return this.processService.createUpdateRequest(this)
    }

    override fun createShutdownRequest(): ProcessShutdownRequest {
        return this.processService.createShutdownRequest(this)
    }

}