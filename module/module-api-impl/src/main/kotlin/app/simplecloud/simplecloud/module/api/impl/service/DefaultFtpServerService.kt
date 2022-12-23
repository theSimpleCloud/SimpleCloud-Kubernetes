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

package app.simplecloud.simplecloud.module.api.impl.service

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.module.api.impl.ftp.FtpServerFactory
import app.simplecloud.simplecloud.module.api.impl.ftp.start.FtpServerStarter
import app.simplecloud.simplecloud.module.api.impl.ftp.stop.FtpServerStopper
import app.simplecloud.simplecloud.module.api.impl.repository.distributed.DistributedFtpServerRepository
import app.simplecloud.simplecloud.module.api.impl.request.ftp.FtpServerCreateRequestImpl
import app.simplecloud.simplecloud.module.api.impl.request.ftp.FtpServerStopRequestImpl
import app.simplecloud.simplecloud.module.api.internal.ftp.FtpServer
import app.simplecloud.simplecloud.module.api.internal.ftp.configuration.FtpCreateConfiguration
import app.simplecloud.simplecloud.module.api.internal.request.ftp.FtpServerCreateRequest
import app.simplecloud.simplecloud.module.api.internal.request.ftp.FtpServerStopRequest
import app.simplecloud.simplecloud.module.api.internal.service.InternalFtpServerService
import java.util.concurrent.CompletableFuture

/**
 * Date: 21.12.22
 * Time: 12:16
 * @author Frederick Baier
 *
 */
class DefaultFtpServerService(
    private val repository: DistributedFtpServerRepository,
    private val factory: FtpServerFactory,
    private val ftpServerStarter: FtpServerStarter,
    private val ftpServerStopper: FtpServerStopper,
) : InternalFtpServerService {

    override fun createCreateRequest(createConfiguration: FtpCreateConfiguration): FtpServerCreateRequest {
        return FtpServerCreateRequestImpl(createConfiguration, this)
    }

    override fun createStopRequest(ftpServer: FtpServer): FtpServerStopRequest {
        return FtpServerStopRequestImpl(ftpServer, this)
    }

    override fun findByName(name: String): CompletableFuture<FtpServer> {
        return this.repository.find(name).thenApply { this.factory.create(it, this) }
    }

    override fun findAll(): CompletableFuture<List<FtpServer>> {
        return this.repository.findAll().thenApply { list -> list.map { this.factory.create(it, this) } }
    }

    override suspend fun createServerInternal(createConfiguration: FtpCreateConfiguration): FtpServer {
        val ftpServer = this.ftpServerStarter.startServer(createConfiguration, this)
        this.repository.save(ftpServer.getName(), ftpServer.toConfiguration()).await()
        return ftpServer
    }

    override suspend fun stopServerInternal(ftpServer: FtpServer) {
        this.ftpServerStopper.stopServer(ftpServer)
        this.repository.remove(ftpServer.getName()).await()
    }


}