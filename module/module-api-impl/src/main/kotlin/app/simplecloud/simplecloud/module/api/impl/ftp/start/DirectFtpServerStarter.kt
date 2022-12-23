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

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.kubernetes.api.KubeAPI
import app.simplecloud.simplecloud.kubernetes.api.Label
import app.simplecloud.simplecloud.kubernetes.api.pod.PodSpec
import app.simplecloud.simplecloud.kubernetes.api.service.ServiceSpec
import app.simplecloud.simplecloud.module.api.impl.ftp.FtpServerFactory
import app.simplecloud.simplecloud.module.api.internal.ftp.FtpServer
import app.simplecloud.simplecloud.module.api.internal.ftp.configuration.FtpCreateConfiguration
import app.simplecloud.simplecloud.module.api.internal.ftp.configuration.FtpServerConfiguration
import app.simplecloud.simplecloud.module.api.internal.service.InternalFtpServerService

class DirectFtpServerStarter(
    private val configuration: FtpCreateConfiguration,
    private val ftpService: InternalFtpServerService,
    private val kubeAPI: KubeAPI,
    private val factory: FtpServerFactory,
) {

    private val networkService = this.kubeAPI.getNetworkService()
    private val podService = this.kubeAPI.getPodService()
    private val label = Label("cloud-ftp-server", this.configuration.ftpServerName.lowercase())

    suspend fun startServer(): FtpServer {
        val port = getNextVacantFtpPort()
        createService(port)
        createFtpServerPod()
        return factory.create(
            FtpServerConfiguration(
                this.configuration.ftpServerName,
                this.configuration.ftpUser,
                this.configuration.ftpPassword,
                this.configuration.volumeClaim.getName(),
                port
            ),
            this.ftpService
        )
    }

    private fun createFtpServerPod() {
        val podSpec = createPodSpec()
        this.podService.createPod("ftp-server-${this.configuration.ftpServerName}".lowercase(), podSpec)
    }

    private fun createPodSpec(): PodSpec {
        val volume = createPodVolume()
        val usernameEnvVariable = createUsernameEnvVariable()
        val passwordEnvVariable = createPasswordEnvVariable()
        return PodSpec()
            .withLabels(this.label)
            .withRestartPolicy("Always")
            .withImage("frederickbaier/ftp")
            .withVolumes(volume)
            .withEnvironmentVariables(usernameEnvVariable, passwordEnvVariable)
            .withMaxMemory(256)
            .withContainerPort(22)
    }

    private fun createUsernameEnvVariable(): PodSpec.EnvironmentVariable {
        return PodSpec.EnvironmentVariable("FTP_USER", this.configuration.ftpUser)
    }

    private fun createPasswordEnvVariable(): PodSpec.EnvironmentVariable {
        return PodSpec.EnvironmentVariable("FTP_PASS", this.configuration.ftpPassword)
    }

    private fun createPodVolume(): PodSpec.MountableVolume {
        return PodSpec.MountableVolume(this.configuration.volumeClaim, "/home/user/volume")
    }

    private fun createService(port: Int) {
        val serviceSpec = createServiceSpec(port)
        this.networkService.createService("ftp-service-${this.configuration.ftpServerName}".lowercase(), serviceSpec)
    }

    private fun createServiceSpec(port: Int): ServiceSpec {
        return ServiceSpec()
            .withLabels(this.label)
            .withContainerPort(22)
            .withClusterPort(22)
            .withPublicPort(port)
    }

    private suspend fun getNextVacantFtpPort(): Int {
        val ftpServers = getAllFtpServers()
        val usedFtpServerPorts = ftpServers.map { it.toConfiguration().port }

        var startPort = FtpServer.FTP_START_PORT
        while (usedFtpServerPorts.contains(startPort)) {
            startPort++
        }
        return startPort
    }

    private suspend fun getAllFtpServers(): List<FtpServer> {
        return this.ftpService.findAll().await()
    }

}
