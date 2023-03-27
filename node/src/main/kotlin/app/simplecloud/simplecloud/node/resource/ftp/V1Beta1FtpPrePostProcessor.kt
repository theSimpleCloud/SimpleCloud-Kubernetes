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

package app.simplecloud.simplecloud.node.resource.ftp

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.service.CloudStateService
import app.simplecloud.simplecloud.api.utils.CloudState
import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeClaimService
import app.simplecloud.simplecloud.module.api.impl.ftp.start.FtpServerStarter
import app.simplecloud.simplecloud.module.api.impl.ftp.stop.FtpServerStopper
import app.simplecloud.simplecloud.module.api.internal.ftp.FtpServer
import app.simplecloud.simplecloud.module.api.internal.ftp.configuration.FtpCreateConfiguration
import app.simplecloud.simplecloud.module.api.internal.service.InternalFtpServerService
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersionRequestPrePostProcessor
import kotlinx.coroutines.runBlocking

/**
 * Date: 23.03.23
 * Time: 09:14
 * @author Frederick Baier
 *
 */
class V1Beta1FtpPrePostProcessor(
    private val cloudStateService: CloudStateService,
    private val ftpServerService: InternalFtpServerService,
    private val kubeVolumeClaimService: KubeVolumeClaimService,
    private val ftpServerStarter: FtpServerStarter,
    private val ftpServerStopper: FtpServerStopper,
) : ResourceVersionRequestPrePostProcessor<V1Beta1FtpSpec>() {


    override fun preCreate(
        group: String,
        version: String,
        kind: String,
        name: String,
        spec: V1Beta1FtpSpec,
    ): RequestPreProcessorResult<V1Beta1FtpSpec> = runBlocking {
        checkCloudDisabled()
        checkConstraint(spec.port in FtpServer.FTP_PORT_RANGE, "Port must be in range ${FtpServer.FTP_PORT_RANGE}")
        checkConstraint(!isPortAlreadyInUse(spec.port), "Port ${spec.port} is already in use")
        checkConstraint(
            kubeVolumeClaimService.doesClaimExist(spec.volumeClaimName),
            "Volume Claim ${spec.volumeClaimName} does not exist"
        )
        checkConstraint(spec.ftpUser.length > 2, "Username is too short (min: 3)")
        checkConstraint(spec.ftpPassword.length > 2, "password is too short (min: 3)")
        return@runBlocking RequestPreProcessorResult.continueNormally()
    }

    private suspend fun isPortAlreadyInUse(port: Int): Boolean {
        val ftpServers = this.ftpServerService.findAll().await()
        val usedFtpServerPorts = ftpServers.map { it.toConfiguration().port }
        return usedFtpServerPorts.contains(port)
    }

    override fun postCreate(group: String, version: String, kind: String, name: String, spec: V1Beta1FtpSpec) {
        runBlocking {
            val kubeVolumeClaim = kubeVolumeClaimService.getClaim(spec.volumeClaimName)
            ftpServerStarter.startServer(
                FtpCreateConfiguration(name, spec.ftpUser, spec.ftpPassword, spec.port, kubeVolumeClaim),
                ftpServerService
            )
        }
    }

    private suspend fun checkCloudDisabled() {
        if (cloudStateService.getCloudState().await() == CloudState.DISABLED) {
            throw IllegalStateException("Unable to perform action. Cloud is disabled.")
        }
    }

    override fun postDelete(group: String, version: String, kind: String, name: String, deletedSpec: V1Beta1FtpSpec) {
        runBlocking {
            val ftpServer = deletedSpec.toConfig(name)
            ftpServerStopper.stopServer(ftpServer)
        }
    }

    override fun preUpdate(
        group: String,
        version: String,
        kind: String,
        name: String,
        spec: V1Beta1FtpSpec,
    ): RequestPreProcessorResult<V1Beta1FtpSpec> {
        return RequestPreProcessorResult.unsupportedRequest()
    }

}