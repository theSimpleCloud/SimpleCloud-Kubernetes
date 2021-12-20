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

package eu.thesimplecloud.simplecloud.kubernetes.impl.container

import eu.thesimplecloud.simplecloud.kubernetes.container.ContainerSpec
import eu.thesimplecloud.simplecloud.api.image.Image
import io.kubernetes.client.custom.Quantity
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.*

class KubernetesContainerStarter(
    private val containerName: String,
    private val image: Image,
    private val containerSpec: ContainerSpec,
    private val api: CoreV1Api
) {

    private val persistentVolumeName = "pvolume-${containerName}"
    private val persistentVolumeClaimName = "claim-${containerName}"

    fun startContainer() {
        createPersistentVolume()
        createPersistentVolumeClaim()
        createPod()
    }

    private fun createPod() {
        val environmentVariables = createEnvironmentVariables()
        val labels = createLabels()
        val containerPort = createContainerPort()

        val pod = V1Pod()
            .metadata(
                V1ObjectMeta()
                    .name(this.containerName)
                    .labels(labels)
            ).spec(
                V1PodSpec()
                    .containers(
                        listOf(
                            V1Container()
                                .name("simplecloud-process")
                                .image(image.getName())
                                .ports(listOf(containerPort))
                                .env(environmentVariables)
                                .resources(
                                    V1ResourceRequirements()
                                        .limits(
                                            hashMapOf(
                                                "memory" to Quantity("${this.containerSpec.maxMemory}Mi")
                                            )
                                        )
                                )
                        )
                    )
            )
        /*
        val pod = V1PodBuilder()
            .withNewMetadata()
            .withName(this.containerName)
            .withLabels(labels)
            .endMetadata()
            .withNewSpec()
            .addNewContainer()
            .withName("simplecloud-process")
            .withImage(image.getName())
            .withPorts(containerPort)
            .withEnv(environmentVariables)
            .withNewResources()
            .withLimits(
                hashMapOf(
                    "memory" to Quantity("${this.containerSpec.maxMemory}Mi")
                )
            )
            .endResources()
            .endContainer()
            .endSpec()
            .build()

         */

        this.api.createNamespacedPod("default", pod, null, null, null)
    }

    private fun createPersistentVolume() {
        val persistentVolume = V1PersistentVolume()
            .metadata(
                V1ObjectMeta()
                    .name(this.persistentVolumeName)
            ).spec(
                V1PersistentVolumeSpec()
                    .storageClassName("standard")
                    .capacity(mapOf("storage" to Quantity.fromString("100Gi")))
                    .accessModes(listOf("ReadWriteOnce"))
                    .claimRef(
                        V1ObjectReference()
                            .name(this.persistentVolumeClaimName)
                    )
            )
        /*
        val persistentVolume = V1PersistentVolumeBuilder()
            .withNewMetadata()
            .withName(this.persistentVolumeName)
            .endMetadata()
            .withNewSpec()
            .withStorageClassName("standard")
            .withCapacity(mapOf("storage" to Quantity.fromString("100Gi")))
            .withAccessModes("ReadWriteOnce")
            .withNewClaimRef()
            .withName(this.persistentVolumeClaimName)
            .endClaimRef()
            .withNewHostPath()
            .withNewPath(PATH_PREFIX + this.persistentVolumeName)
            .endHostPath()
            .endSpec()
            .build()

         */
        this.api.createPersistentVolume(persistentVolume, null, null, null)
    }

    private fun createPersistentVolumeClaim() {
        val persistentVolumeClaim = V1PersistentVolumeClaim()
            .metadata(V1ObjectMeta().name(this.persistentVolumeClaimName))
            .spec(
                V1PersistentVolumeClaimSpec()
                    .storageClassName("standard")
                    .accessModes(listOf("ReadWriteOnce"))
                    .resources(
                        V1ResourceRequirements()
                            .requests(mapOf("storage" to Quantity.fromString("100Gi")))
                    )
            )
        /*
        val persistentVolumeClaim = V1PersistentVolumeClaimBuilder()
            .withNewMetadata()
            .withName(this.persistentVolumeClaimName)
            .endMetadata()
            .withNewSpec()
            .withStorageClassName("standard")
            .withAccessModes("ReadWriteOnce")
            .withNewResources()
            .withRequests(mapOf("storage" to Quantity.fromString("100Gi")))
            .endResources()
            .endSpec()
            .build()

         */
        this.api.createNamespacedPersistentVolumeClaim("default", persistentVolumeClaim, null, null, null)
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