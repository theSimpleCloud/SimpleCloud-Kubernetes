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

package app.simplecloud.simplecloud.module.api.impl.ftp.start

import app.simplecloud.simplecloud.kubernetes.api.KubeAPI
import app.simplecloud.simplecloud.module.api.impl.ftp.FtpServerFactory
import app.simplecloud.simplecloud.module.api.internal.ftp.FtpServer
import app.simplecloud.simplecloud.module.api.internal.ftp.configuration.FtpCreateConfiguration
import app.simplecloud.simplecloud.module.api.internal.service.InternalFtpServerService

/**
 * Date: 21.12.22
 * Time: 17:23
 * @author Frederick Baier
 *
 */
class FtpServerStarterImpl(
    private val kubeAPI: KubeAPI,
    private val factory: FtpServerFactory,
) : FtpServerStarter {

    override suspend fun startServer(
        configuration: FtpCreateConfiguration,
        service: InternalFtpServerService,
    ): FtpServer {
        return DirectFtpServerStarter(configuration, service, this.kubeAPI, this.factory).startServer()
    }

}