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

import app.simplecloud.simplecloud.api.impl.process.factory.CloudProcessFactory
import app.simplecloud.simplecloud.api.impl.repository.ignite.IgniteCloudProcessRepository
import app.simplecloud.simplecloud.api.impl.service.AbstractCloudProcessService
import app.simplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.node.process.InternalProcessShutdownHandler
import app.simplecloud.simplecloud.node.process.InternalProcessStartHandler
import app.simplecloud.simplecloud.node.process.ProcessShutdownHandler
import app.simplecloud.simplecloud.node.process.ProcessStarter
import com.google.inject.Inject
import com.google.inject.Singleton

@Singleton
class CloudProcessServiceImpl @Inject constructor(
    processFactory: CloudProcessFactory,
    igniteRepository: IgniteCloudProcessRepository,
    private val processStarterFactory: ProcessStarter.Factory,
    private val processShutdownHandlerFactory: ProcessShutdownHandler.Factory
) : AbstractCloudProcessService(
    processFactory, igniteRepository
) {
    override suspend fun startNewProcessInternal(configuration: ProcessStartConfiguration): CloudProcess {
        return InternalProcessStartHandler(configuration, this, this.processStarterFactory)
            .startProcess()
    }

    override suspend fun shutdownProcessInternal(process: CloudProcess) {
        return InternalProcessShutdownHandler(process, this.processShutdownHandlerFactory)
            .shutdownProcess()
    }
}