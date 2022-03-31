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

package app.simplecloud.simplecloud.node.task

import app.simplecloud.simplecloud.api.future.unitFuture
import app.simplecloud.simplecloud.api.impl.util.ClusterKey
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.kubernetes.api.Label
import app.simplecloud.simplecloud.kubernetes.api.container.Container
import app.simplecloud.simplecloud.kubernetes.api.container.ContainerSpec
import app.simplecloud.simplecloud.kubernetes.api.service.KubeService
import app.simplecloud.simplecloud.kubernetes.api.service.ServiceSpec
import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeClaim
import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeSpec
import org.apache.logging.log4j.LogManager
import java.util.concurrent.CompletableFuture

class KubernetesProcessStarter(
    private val process: CloudProcess,
    private val containerFactory: Container.Factory,
    private val volumeFactory: KubeVolumeClaim.Factory,
    private val serviceFactory: KubeService.Factory,
    private val clusterKey: ClusterKey,
) {

    private val processLabel = Label("cloud-process", this.process.getName())
    private val groupLabel = Label("cloud-group", this.process.getGroupName())

    fun startProcess(): CompletableFuture<Unit> {
        logger.info("Starting Process {}", process.getName())

        startContainer()
        createServiceForProcess()
        return unitFuture()
    }

    private fun createServiceForProcess() {
        this.serviceFactory.create(
            this.process.getName(),
            ServiceSpec().withContainerPort(25565)
                .withClusterPort(25565)
                .withLabels(this.processLabel)
        )
    }

    private fun startContainer() {
        val container = createContainer()
        val containerSpecifications = createContainerSpecifications()
        container.start(containerSpecifications)
    }

    private fun createContainerSpecifications(): ContainerSpec {
        //val volume = createVolumeClaim()

        val processUniqueIdEnvironment = createProcessIdEnvironmentVariable()
        val clusterKeyEnvironment = createClusterKeyEnvironmentVariable()
        return ContainerSpec()
            .withContainerPort(25565)
            .withMaxMemory(this.process.getMaxMemory())
            .withLabels(this.processLabel, this.groupLabel)
            //.withVolumes(ContainerSpec.MountableVolume(volume, "/data"))
            .withEnvironmentVariables(clusterKeyEnvironment, processUniqueIdEnvironment)
            .withImage(this.process.getImage().getName())
    }

    private fun createContainer(): Container {

        return this.containerFactory.create(
            this.process.getName()
        )
    }

    private fun createClusterKeyEnvironmentVariable(): ContainerSpec.EnvironmentVariable {
        return ContainerSpec.EnvironmentVariable(
            "CLUSTER_KEY",
            this.clusterKey.login + ":" + this.clusterKey.password
        )
    }

    private fun createProcessIdEnvironmentVariable(): ContainerSpec.EnvironmentVariable {
        return ContainerSpec.EnvironmentVariable(
            "SIMPLECLOUD_PROCESS_ID",
            this.process.getUniqueId().toString()
        )
    }

    private fun createVolumeClaim(): KubeVolumeClaim {
        return this.volumeFactory.create(
            "claim-" + this.process.getName(),
            KubeVolumeSpec()
                .withStorageClassName("microk8s-hostpath")
                .withRequestedStorageInGB(1)
        )
    }

    companion object {
        private val logger = LogManager.getLogger(KubernetesProcessStarter::class.java)
    }

}