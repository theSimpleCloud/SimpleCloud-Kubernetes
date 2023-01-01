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

package app.simplecloud.simplecloud.kubernetes.impl.pod

import app.simplecloud.simplecloud.kubernetes.api.exception.KubeException
import app.simplecloud.simplecloud.kubernetes.api.pod.PodSpec
import io.kubernetes.client.custom.Quantity
import io.kubernetes.client.openapi.ApiException
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.*

class KubernetesPodStarter(
    private val containerName: String,
    private val containerSpec: PodSpec,
    private val api: CoreV1Api,
) {

    fun startContainer() {
        createPod()
    }

    private fun createPod() {
        val environmentVariables = createEnvironmentVariables()
        val labels = createLabels()
        val containerPort = createContainerPort()
        val volumes = createVolumes()
        val volumeMounts = createVolumeMounts()

        val image = containerSpec.image
        require(image != null) { "Image must be not null" }
        val container = V1Container()
            .name("simplecloud-process")
            .image(image.lowercase())
            .env(environmentVariables)
            .stdin(true)
            .tty(true)
            .resources(
                V1ResourceRequirements()
                    .requests(
                        hashMapOf(
                            "memory" to Quantity.fromString("${this.containerSpec.maxMemory}Mi")
                        )
                    )
            ).volumeMounts(
                volumeMounts
            )

        if (containerPort != null)
            container.ports(listOf(containerPort))

        val kubeCommand = containerSpec.command
        if (kubeCommand != null) {
            container.command(kubeCommand.command)
            container.args(kubeCommand.args)
        }

        val pod = V1Pod()
            .metadata(
                V1ObjectMeta()
                    .name(this.containerName.lowercase())
                    .labels(labels)
            ).spec(
                V1PodSpec()
                    .containers(
                        listOf(
                            container
                        )
                    ).volumes(
                        volumes
                    ).restartPolicy(this.containerSpec.restartPolicy)
            )
        createNamespacedPod(pod)
    }

    private fun createNamespacedPod(pod: V1Pod) {
        try {
            this.api.createNamespacedPod("default", pod, null, null, null, null)
        } catch (ex: ApiException) {
            throw KubeException(ex.responseBody, ex)
        }
    }

    private fun createVolumes(): List<V1Volume> {
        return this.containerSpec.volumes.map { it.volumeClaim }
            .map {
                V1Volume()
                    .name(it.getName())
                    .persistentVolumeClaim(
                        V1PersistentVolumeClaimVolumeSource()
                            .claimName(it.getName())
                    )
            }
    }

    private fun createVolumeMounts(): List<V1VolumeMount> {
        return this.containerSpec.volumes.map {
            V1VolumeMount()
                .name(it.volumeClaim.getName())
                .mountPath(it.mountPath)
        }
    }

    private fun createContainerPort(): V1ContainerPort? {
        if (this.containerSpec.containerPort == -1)
            return null
        return V1ContainerPort()
            .containerPort(this.containerSpec.containerPort)
    }

    private fun createLabels(): Map<String, String> {
        return this.containerSpec.labels.associate { it.getNamePair() }
    }

    private fun createEnvironmentVariables(): List<V1EnvVar> {
        return this.containerSpec.environmentVariables
            .map { V1EnvVar().name(it.name).value(it.value) }
    }

}