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

package app.simplecloud.simplecloud.kubernetes.impl.service

import app.simplecloud.simplecloud.kubernetes.api.service.KubeService
import app.simplecloud.simplecloud.kubernetes.api.service.ServiceSpec
import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import io.kubernetes.client.custom.IntOrString
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1ObjectMeta
import io.kubernetes.client.openapi.models.V1Service
import io.kubernetes.client.openapi.models.V1ServicePort
import io.kubernetes.client.openapi.models.V1ServiceSpec

class KubeServiceImpl @Inject constructor(
    @Assisted name: String,
    @Assisted private val serviceSpec: ServiceSpec,
    private val api: CoreV1Api
) : KubeService {

    private val name: String = name.lowercase()

    init {
        createServiceIfNotExist()
    }

    private fun createServiceIfNotExist() {
        if (doesServiceExist()) {
            delete()
        }
        createService()
    }

    override fun delete() {
        this.api.deleteNamespacedService(
            this.name,
            "default",
            null,
            null,
            null,
            null,
            null,
            null
        )
    }

    private fun doesServiceExist(): Boolean {
        return runCatching {
            this.api.readNamespacedService(this.name, "default", null)
        }.isSuccess
    }

    private fun createService() {
        val service = createServiceObj()
        this.api.createNamespacedService("default", service, null, null, null)
    }

    private fun createServiceObj(): V1Service {
        val labels = this.serviceSpec.labels.associate { it.getNamePair() }
        val port = createServicePort()
        val type = generateType()
        return V1Service()
            .metadata(V1ObjectMeta().name(this.name))
            .spec(
                V1ServiceSpec()
                    .type(type)
                    .selector(labels)
                    .ports(
                        listOf(
                            port
                        )
                    )
            )
    }

    private fun createServicePort(): V1ServicePort {
        val port = V1ServicePort()
            .protocol("TCP")
            .port(this.serviceSpec.clusterPort)
            .targetPort(IntOrString(this.serviceSpec.containerPort))

        if (this.serviceSpec.publicPort != -1)
            port.nodePort(this.serviceSpec.publicPort)
        return port
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