/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
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
        container.start()
    }

    private fun createContainer(): Container {
        val volume = createVolumeClaim()

        val processUniqueIdEnvironment = createProcessIdEnvironmentVariable()
        val clusterKeyEnvironment = createClusterKeyEnvironmentVariable()

        return this.containerFactory.create(
            this.process.getName(),
            this.process.getImage(),
            ContainerSpec()
                .withContainerPort(25565)
                .withMaxMemory(this.process.getMaxMemory())
                .withLabels(this.processLabel, this.groupLabel)
                .withVolumes(ContainerSpec.MountableVolume(volume, "/data"))
                .withEnvironmentVariables(clusterKeyEnvironment, processUniqueIdEnvironment)

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