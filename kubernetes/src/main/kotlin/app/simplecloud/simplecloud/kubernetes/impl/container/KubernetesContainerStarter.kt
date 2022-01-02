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

package app.simplecloud.simplecloud.kubernetes.impl.container

import app.simplecloud.simplecloud.api.image.Image
import app.simplecloud.simplecloud.kubernetes.api.container.ContainerSpec
import io.kubernetes.client.custom.Quantity
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.*

class KubernetesContainerStarter(
    private val containerName: String,
    private val image: Image,
    private val containerSpec: ContainerSpec,
    private val api: CoreV1Api
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

        val pod = V1Pod()
            .metadata(
                V1ObjectMeta()
                    .name(this.containerName.lowercase())
                    .labels(labels)
            ).spec(
                V1PodSpec()
                    .containers(
                        listOf(
                            V1Container()
                                .name("simplecloud-process")
                                .image(image.getName().lowercase())
                                .ports(listOf(containerPort))
                                .env(environmentVariables)
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
                        )
                    ).volumes(
                        volumes
                    )
            )

        this.api.createNamespacedPod("default", pod, null, null, null)
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

    private fun createContainerPort(): V1ContainerPort {
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