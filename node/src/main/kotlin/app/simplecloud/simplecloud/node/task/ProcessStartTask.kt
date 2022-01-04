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

class ProcessStartTask(
    private val process: CloudProcess,
    private val containerFactory: Container.Factory,
    private val volumeFactory: KubeVolumeClaim.Factory,
    private val serviceFactory: KubeService.Factory,
    private val clusterKey: ClusterKey,
) {

    fun run(): CompletableFuture<Unit> {
        logger.info("Starting Process {}", process.getName())

        val volume = this.volumeFactory.create(
            "claim-" + this.process.getName(),
            KubeVolumeSpec()
                .withStorageClassName("microk8s-hostpath")
                .withRequestedStorageInGB(1)
        )

        val processUniqueIdEnvironment = ContainerSpec.EnvironmentVariable(
            "SIMPLECLOUD_PROCESS_ID",
            process.getUniqueId().toString()
        )
        val clusterKeyEnvironment = ContainerSpec.EnvironmentVariable(
            "CLUSTER_KEY",
            clusterKey.login + ":" + clusterKey.password
        )
        val label = Label("process-${this.process.getName()}")
        val container = containerFactory.create(
            process.getName(),
            process.getImage(),
            ContainerSpec()
                .withContainerPort(25565)
                .withMaxMemory(process.getMaxMemory())
                .withLabels(label)
                .withVolumes(ContainerSpec.MountableVolume(volume, "/data"))
                .withEnvironmentVariables(clusterKeyEnvironment, processUniqueIdEnvironment)

        )
        container.start()

        this.serviceFactory.create(
            this.process.getName(),
            ServiceSpec().withContainerPort(25565)
                .withClusterPort(25565)
                .withLabels(label)
                .withPublicPort(30009)
        )
        return unitFuture()
    }


    companion object {
        private val logger = LogManager.getLogger(ProcessStartTask::class.java)
    }

}