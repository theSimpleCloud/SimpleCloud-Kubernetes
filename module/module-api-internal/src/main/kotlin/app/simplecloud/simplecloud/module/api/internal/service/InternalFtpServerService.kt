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

package app.simplecloud.simplecloud.module.api.internal.service

import app.simplecloud.simplecloud.module.api.internal.ftp.FtpServer
import app.simplecloud.simplecloud.module.api.internal.ftp.configuration.FtpCreateConfiguration
import app.simplecloud.simplecloud.module.api.internal.request.ftp.FtpServerCreateRequest
import app.simplecloud.simplecloud.module.api.internal.request.ftp.FtpServerStopRequest
import java.util.concurrent.CompletableFuture

/**
 * Date: 21.12.22
 * Time: 09:17
 * @author Frederick Baier
 *
 */
//There is no FtpServerService, because this service should not be available to normal modules
interface InternalFtpServerService {

    fun createCreateRequest(createConfiguration: FtpCreateConfiguration): FtpServerCreateRequest

    fun createStopRequest(ftpServer: FtpServer): FtpServerStopRequest

    fun findByName(name: String): CompletableFuture<FtpServer>

    fun findAll(): CompletableFuture<List<FtpServer>>

    suspend fun createServerInternal(createConfiguration: FtpCreateConfiguration): FtpServer

    suspend fun stopServerInternal(ftpServer: FtpServer)

}