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

package app.simplecloud.simplecloud.node.service

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.api.impl.process.factory.CloudProcessFactory
import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedCloudProcessRepository
import app.simplecloud.simplecloud.api.impl.service.AbstractCloudProcessService
import app.simplecloud.simplecloud.api.internal.configutation.ProcessExecuteCommandConfiguration
import app.simplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.eventapi.EventManager
import app.simplecloud.simplecloud.kubernetes.api.pod.KubePodService
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.ResourceRequestHandler
import app.simplecloud.simplecloud.node.process.InternalProcessStartRequestSender
import app.simplecloud.simplecloud.node.resource.process.execute.V1Beta1CloudProcessExecuteBody
import java.util.concurrent.CompletableFuture

class CloudProcessServiceImpl(
    processFactory: CloudProcessFactory,
    distributedRepository: DistributedCloudProcessRepository,
    eventManager: EventManager,
    private val podService: KubePodService,
    private val requestHandler: ResourceRequestHandler,
) : AbstractCloudProcessService(
    processFactory, distributedRepository, eventManager
) {
    override suspend fun startNewProcessInternal(configuration: ProcessStartConfiguration): CloudProcess {
        return InternalProcessStartRequestSender(configuration, this, this.requestHandler).sendStartRequest()
    }

    override suspend fun shutdownProcessInternal(process: CloudProcess) {
        this.requestHandler.handleDelete("core", "CloudProcess", "v1beta1", process.getName())
    }

    override suspend fun executeCommandInternal(configuration: ProcessExecuteCommandConfiguration) {
        this.requestHandler.handleCustomAction(
            "core",
            "CloudProcess",
            "v1beta1",
            configuration.processName,
            "execute",
            V1Beta1CloudProcessExecuteBody(configuration.command)
        )
    }

    override fun getLogs(process: CloudProcess): CompletableFuture<String> = CloudScope.future {
        return@future podService.getPod(process.getName()).getLogs()
    }
}