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

package app.simplecloud.simplecloud.plugin.startup.service

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.impl.process.factory.CloudProcessFactory
import app.simplecloud.simplecloud.api.impl.repository.ignite.IgniteCloudProcessRepository
import app.simplecloud.simplecloud.api.impl.service.AbstractCloudProcessService
import app.simplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import app.simplecloud.simplecloud.api.messagechannel.manager.MessageChannelManager
import app.simplecloud.simplecloud.api.node.Node
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.process.CloudProcessConfiguration
import app.simplecloud.simplecloud.api.service.NodeService
import com.google.inject.Inject
import com.google.inject.Singleton

@Singleton
class CloudProcessServiceImpl @Inject constructor(
    processFactory: CloudProcessFactory,
    igniteRepository: IgniteCloudProcessRepository,
    private val messageChannelManager: MessageChannelManager,
    private val nodeService: NodeService
) : AbstractCloudProcessService(
    processFactory, igniteRepository
) {

    private val startProcessMessageChannel =
        this.messageChannelManager.getOrCreateMessageChannel<ProcessStartConfiguration, CloudProcessConfiguration>("internal_start_process")

    override suspend fun startNewProcessInternal(configuration: ProcessStartConfiguration): CloudProcess {
        val node = this.nodeService.findFirst().await()
        val processConfiguration = sendStartRequestToNode(configuration, node)
        return this.processFactory.create(processConfiguration)
    }

    private suspend fun sendStartRequestToNode(configuration: ProcessStartConfiguration, node: Node): CloudProcessConfiguration {
        return this.startProcessMessageChannel.createMessageRequest(configuration, node).submit().await()
    }

    override suspend fun shutdownProcessInternal(process: CloudProcess) {
        TODO()
    }
}