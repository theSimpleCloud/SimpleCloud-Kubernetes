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

package eu.thesimplecloud.simplecloud.kubernetes.impl.service

import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import eu.thesimplecloud.simplecloud.kubernetes.service.KubeService
import eu.thesimplecloud.simplecloud.kubernetes.service.ServiceSpec
import io.kubernetes.client.custom.IntOrString
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1ObjectMeta
import io.kubernetes.client.openapi.models.V1Service
import io.kubernetes.client.openapi.models.V1ServicePort
import io.kubernetes.client.openapi.models.V1ServiceSpec

class KubeServiceImpl @Inject constructor(
    @Assisted private val name: String,
    @Assisted private val serviceSpec: ServiceSpec,
    private val api: CoreV1Api
) : KubeService {

    init {
        createServiceIfNotExist()
    }

    private fun createServiceIfNotExist() {
        if (!doesServiceExist())
            createService()
    }

    private fun doesServiceExist(): Boolean {
        val result = this.api.readNamespacedService(this.name, "default", null)
        println(result)
        return false
    }

    private fun createService() {
        val labels = this.serviceSpec.labels.associate { it.getNamePair() }
        val type = generateType()
        val service = V1Service()
            .metadata(V1ObjectMeta().name(this.name))
            .spec(
                V1ServiceSpec()
                    .type(type)
                    .selector(labels)
                    .ports(
                        listOf(
                            V1ServicePort()
                                .protocol("TCP")
                                .port(this.serviceSpec.clusterPort)
                                .targetPort(IntOrString(this.serviceSpec.containerPort))
                                .nodePort(this.serviceSpec.publicPort)
                        )
                    )
            )
        /*
        val service = V1ServiceBuilder()
            .withNewMetadata()
            .withName(this.name)
            .endMetadata()
            .withNewSpec()
            .withType(type)
            .withSelector(labels)
            .addNewPort()
            .withProtocol("TCP")
            .withPort(this.serviceSpec.clusterPort)
            .withTargetPort(IntOrString(this.serviceSpec.containerPort))
            .withNodePort(this.serviceSpec.publicPort)
            .endPort()
            .endSpec()
            .build()

         */
        this.api.createNamespacedService("default", service, null, null, null)
    }

    private fun generateType(): String {
        if (this.serviceSpec.publicPort == -1) {
            return "ClusterIP"
        }
        return "NodePort"
    }


    override fun getName(): String {
        return this.name
    }
}