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
import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.module.api.impl.ftp.FtpServerFactory
import app.simplecloud.simplecloud.module.api.impl.request.ftp.FtpServerCreateRequestImpl
import app.simplecloud.simplecloud.module.api.impl.request.ftp.FtpServerStopRequestImpl
import app.simplecloud.simplecloud.module.api.internal.ftp.FtpServer
import app.simplecloud.simplecloud.module.api.internal.ftp.configuration.FtpCreateConfiguration
import app.simplecloud.simplecloud.module.api.internal.request.ftp.FtpServerCreateRequest
import app.simplecloud.simplecloud.module.api.internal.request.ftp.FtpServerStopRequest
import app.simplecloud.simplecloud.module.api.internal.service.InternalFtpServerService
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.ResourceRequestHandler
import app.simplecloud.simplecloud.node.resource.ftp.V1Beta1FtpSpec
import java.util.concurrent.CompletableFuture

/**
 * Date: 21.12.22
 * Time: 12:16
 * @author Frederick Baier
 *
 */
class DefaultFtpServerService(
    private val factory: FtpServerFactory,
    private val requestHandler: ResourceRequestHandler,
) : InternalFtpServerService {

    override fun createCreateRequest(createConfiguration: FtpCreateConfiguration): FtpServerCreateRequest {
        return FtpServerCreateRequestImpl(createConfiguration, this)
    }

    override fun createStopRequest(ftpServer: FtpServer): FtpServerStopRequest {
        return FtpServerStopRequestImpl(ftpServer, this)
    }

    override fun findByName(name: String): CompletableFuture<FtpServer> = CloudScope.future {
        val spec = requestHandler.handleGetOneSpec<V1Beta1FtpSpec>(
            "core",
            "FtpServer",
            "v1beta1",
            name
        ).getSpec()
        val ftpServerConfiguration = spec.toConfig(name)
        return@future factory.create(ftpServerConfiguration, this@DefaultFtpServerService)
    }

    override fun findAll(): CompletableFuture<List<FtpServer>> = CloudScope.future {
        val ftpServerServerResults = requestHandler.handleGetAllSpec<V1Beta1FtpSpec>("core", "FtpServer", "v1beta1")
        val ftpServerConfigurations = ftpServerServerResults.map { it.getSpec().toConfig(it.getName()) }
        return@future ftpServerConfigurations.map { factory.create(it, this@DefaultFtpServerService) }
    }

    override suspend fun createServerInternal(createConfiguration: FtpCreateConfiguration): FtpServer {
        requestHandler.handleCreate(
            "core", "FtpServer", "v1beta1", createConfiguration.ftpServerName, V1Beta1FtpSpec(
                createConfiguration.volumeClaim.getName(),
                createConfiguration.ftpUser,
                createConfiguration.ftpPassword,
                createConfiguration.port
            )
        )
        return findByName(createConfiguration.ftpServerName).await()
    }

    override suspend fun stopServerInternal(ftpServer: FtpServer) {
        requestHandler.handleDelete("core", "FtpServer", "v1beta1", ftpServer.getName())
    }


}