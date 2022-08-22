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

import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.kubernetes.api.KubeAPI
import app.simplecloud.simplecloud.kubernetes.api.Label
import app.simplecloud.simplecloud.kubernetes.api.pod.KubePod
import app.simplecloud.simplecloud.kubernetes.api.pod.PodSpec
import app.simplecloud.simplecloud.kubernetes.api.service.ServiceSpec
import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeClaim
import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeSpec
import org.apache.logging.log4j.LogManager

class KubernetesProcessStarter(
    private val process: CloudProcess,
    private val kubeAPI: KubeAPI
) {

    private val networkService = this.kubeAPI.getNetworkService()
    private val podService = this.kubeAPI.getPodService()
    private val volumeClaimService = this.kubeAPI.getVolumeClaimService()

    private val processLabel = Label("cloud-process", this.process.getName())
    private val groupLabel = Label("cloud-group", this.process.getGroupName())

    fun startProcess() {
        logger.info("Starting Process {}", process.getName())

        createPod()
        recreateServiceForProcess()
    }

    private fun recreateServiceForProcess() {
        deleteServiceIfExist(this.process.getName())
        this.networkService.createService(
            this.process.getName(),
            ServiceSpec().withContainerPort(25565)
                .withClusterPort(25565)
                .withLabels(this.processLabel)
        )
    }

    private fun deleteServiceIfExist(name: String) {
        try {
            val service = this.networkService.getService(name)
            service.delete()
        } catch (_: NoSuchElementException) {

        }
    }

    private fun createContainerSpecifications(): PodSpec {
        //val volume = createVolumeClaim()

        val processUniqueIdEnvironment = createProcessIdEnvironmentVariable()
        return PodSpec()
            .withContainerPort(25565)
            .withMaxMemory(this.process.getMaxMemory())
            .withLabels(this.processLabel, this.groupLabel)
            //.withVolumes(ContainerSpec.MountableVolume(volume, "/data"))
            .withEnvironmentVariables(processUniqueIdEnvironment)
            .withImage(this.process.getImage().getName())
            .withRestartPolicy("Never")
    }

    private fun createPod(): KubePod {
        val podSpec = createContainerSpecifications()
        return this.podService.createPod(
            this.process.getName(),
            podSpec
        )
    }

    private fun createProcessIdEnvironmentVariable(): PodSpec.EnvironmentVariable {
        return PodSpec.EnvironmentVariable(
            "SIMPLECLOUD_PROCESS_ID",
            this.process.getUniqueId().toString()
        )
    }

    private fun createVolumeClaim(): KubeVolumeClaim {
        return this.volumeClaimService.createVolumeClaim(
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