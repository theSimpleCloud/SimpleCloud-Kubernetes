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
import app.simplecloud.simplecloud.api.process.state.ProcessState
import app.simplecloud.simplecloud.api.request.process.ProcessExecuteCommandRequest
import app.simplecloud.simplecloud.api.request.process.ProcessShutdownRequest
import app.simplecloud.simplecloud.api.request.process.ProcessUpdateRequest
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.api.template.ProcessTemplateType
import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.distribution.api.DistributionComponent
import app.simplecloud.simplecloud.distribution.api.impl.ClientComponentImpl
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 27.03.2021
 * Time: 09:14
 * @author Frederick Baier
 */
class CloudProcessImpl constructor(
    private val configuration: CloudProcessConfiguration,
    private val processService: CloudProcessService
) : CloudProcess {

    override fun getGroupName(): String {
        return this.configuration.processTemplateName
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

    override fun getProcessType(): ProcessTemplateType {
        return this.configuration.processTemplateType
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
        return this.configuration.getProcessName()
    }

    override fun getIdentifier(): String {
        return getName()
    }

    override fun toConfiguration(): CloudProcessConfiguration {
        return this.configuration
    }

    override fun getDistributionComponent(): DistributionComponent {
        val distributionId = this.configuration.distributionId
            ?: throw IllegalStateException("Process must be running")
        return ClientComponentImpl(distributionId)
    }

    override fun creatExecuteCommandRequest(command: String): ProcessExecuteCommandRequest {
        return this.processService.createExecuteCommandRequest(this, command)
    }

    override fun getLogs(): CompletableFuture<List<String>> {
        return this.processService.getLogs(this)
    }

    override fun createUpdateRequest(): ProcessUpdateRequest {
        return this.processService.createUpdateRequest(this)
    }

    override fun createShutdownRequest(): ProcessShutdownRequest {
        return this.processService.createShutdownRequest(this)
    }

}