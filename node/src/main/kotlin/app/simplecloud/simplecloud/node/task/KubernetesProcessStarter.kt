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
    private val kubeAPI: KubeAPI,
) {

    private val networkService = this.kubeAPI.getNetworkService()
    private val podService = this.kubeAPI.getPodService()
    private val volumeClaimService = this.kubeAPI.getVolumeClaimService()

    private val processLabel = Label("cloud-process", this.process.getName())
    private val groupLabel = Label("cloud-group", this.process.getProcessTemplateName())

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
        val processUniqueIdEnvironment = createProcessIdEnvironmentVariable()
        val podSpec = PodSpec().withContainerPort(25565)
            .withMaxMemory(this.process.getMaxMemory())
            .withLabels(this.processLabel, this.groupLabel)
            .withEnvironmentVariables(processUniqueIdEnvironment)
            .withImage(this.process.getImage().getName())
            .withRestartPolicy("Never")

        if (this.process.isStatic()) {
            val volume = getOrCreateVolumeClaim()
            podSpec.withVolumes(PodSpec.MountableVolume(volume, "/static"))
        }
        return podSpec
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

    private fun getOrCreateVolumeClaim(): KubeVolumeClaim {
        val claimName = ("claim-" + this.process.getName()).lowercase()
        val existingVolumeClaim = getExistingVolumeClaimOrNull(claimName)
        if (existingVolumeClaim != null)
            return existingVolumeClaim

        return createVolumeClaim(claimName)
    }

    private fun createVolumeClaim(claimName: String): KubeVolumeClaim {
        return this.volumeClaimService.createVolumeClaim(
            claimName,
            KubeVolumeSpec()
                .withStorageClassName("microk8s-hostpath")
                .withRequestedStorageInGB(1)
        )
    }

    private fun getExistingVolumeClaimOrNull(claimName: String): KubeVolumeClaim? {
        return try {
            this.volumeClaimService.getClaim(claimName)
        } catch (e: NoSuchElementException) {
            null
        }
    }

    companion object {
        private val logger = LogManager.getLogger(KubernetesProcessStarter::class.java)
    }

}