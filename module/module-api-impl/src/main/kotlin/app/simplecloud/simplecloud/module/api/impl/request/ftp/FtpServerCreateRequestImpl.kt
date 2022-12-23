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

package app.simplecloud.simplecloud.module.api.impl.request.ftp

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.module.api.internal.ftp.FtpServer
import app.simplecloud.simplecloud.module.api.internal.ftp.configuration.FtpCreateConfiguration
import app.simplecloud.simplecloud.module.api.internal.request.ftp.FtpServerCreateRequest
import app.simplecloud.simplecloud.module.api.internal.service.InternalFtpServerService
import java.util.concurrent.CompletableFuture

/**
 * Date: 21.12.22
 * Time: 13:56
 * @author Frederick Baier
 *
 */
class FtpServerCreateRequestImpl(
    private val configuration: FtpCreateConfiguration,
    private val internalService: InternalFtpServerService,
) : FtpServerCreateRequest {

    override fun submit(): CompletableFuture<FtpServer> = CloudScope.future {
        return@future internalService.createServerInternal(configuration)
    }

}