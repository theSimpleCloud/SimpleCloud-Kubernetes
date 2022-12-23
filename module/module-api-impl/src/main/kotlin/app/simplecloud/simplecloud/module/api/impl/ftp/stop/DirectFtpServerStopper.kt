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

package app.simplecloud.simplecloud.module.api.impl.ftp.stop

import app.simplecloud.simplecloud.kubernetes.api.KubeAPI
import app.simplecloud.simplecloud.module.api.internal.ftp.FtpServer

class DirectFtpServerStopper(
    private val ftpServer: FtpServer,
    private val kubeAPI: KubeAPI,
) {


    private val networkService = this.kubeAPI.getNetworkService()

    private val podService = this.kubeAPI.getPodService()

    suspend fun stopServer() {
        val configuration = this.ftpServer.toConfiguration()
        this.networkService.getService("ftp-service-${configuration.ftpServerName}".lowercase())
            .delete()

        this.podService.getPod("ftp-server-${configuration.ftpServerName}".lowercase())
            .delete()
    }

}
