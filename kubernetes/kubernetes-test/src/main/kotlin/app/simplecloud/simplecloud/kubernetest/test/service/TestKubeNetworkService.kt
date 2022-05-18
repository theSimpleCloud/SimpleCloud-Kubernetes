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

package app.simplecloud.simplecloud.kubernetest.test.service

import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.kubernetes.api.Label
import app.simplecloud.simplecloud.kubernetes.api.pod.KubePod
import app.simplecloud.simplecloud.kubernetes.api.service.KubeNetworkService
import app.simplecloud.simplecloud.kubernetes.api.service.KubeService
import app.simplecloud.simplecloud.kubernetes.api.service.ServiceSpec
import app.simplecloud.simplecloud.kubernetest.test.pod.TestKubePodService
import java.net.BindException
import java.util.concurrent.CopyOnWriteArrayList

class TestKubeNetworkService(
    private val testKubePodService: TestKubePodService
) : KubeNetworkService {

    private val services = CopyOnWriteArrayList<KubeService>()

    private val podBinds = CopyOnWriteArrayList<PodBind>()

    override fun createService(name: String, serviceSpec: ServiceSpec): KubeService {
        checkAlreadyExist(name.lowercase())
        val kubeService = TestKubeService(name.lowercase(), serviceSpec, this)
        this.services.add(kubeService)
        return kubeService
    }

    override fun getService(name: String): KubeService {
        return this.services.first { it.getName() == name.lowercase() }
    }

    override fun translateAddress(address: Address): Address {
        val service = findService(address)
        val pod = findRandomPodByService(service)
        return getActualAddressFromPod(pod, service)
    }

    private fun getActualAddressFromPod(pod: KubePod, service: KubeService): Address {
        val forwardingContainerPort = service.getContainerPort()
        val podBinds = this.podBinds.filter { it.pod == pod }
        val actualPort = podBinds.first { it.requestedPort == forwardingContainerPort }.actualPort
        return Address("127.0.0.1", actualPort)
    }

    private fun findRandomPodByService(service: KubeService): KubePod {
        val pods = findPodsWithMatchingPortAndLabels(service)
        if (pods.isEmpty())
            throw KubeNetworkService.AddressTranslationException("No matching pod found")
        return pods.random()
    }

    private fun findPodsWithMatchingPortAndLabels(service: KubeService): List<KubePod> {
        val podsWithMatchingLabel = findPodsByLabels(service.getLabels())
        val forwardingContainerPort = service.getContainerPort()
        return podsWithMatchingLabel.filter { hasPortBound(it, forwardingContainerPort) }
    }

    private fun findService(address: Address): KubeService {
        val service = getService(address.host)
        if (service.getClusterPort() != address.port) {
            throw KubeNetworkService.AddressTranslationException(
                "Port of address '${address.port}' does not match the service cluster port '${service.getClusterPort()}'"
            )
        }
        return service
    }

    private fun hasPortBound(pod: KubePod, port: Int): Boolean {
        val podBinds = this.podBinds.filter { it.pod == pod }
        return podBinds.any { it.requestedPort == port }
    }

    private fun findPodsByLabels(labels: List<Label>): List<KubePod> {
        return labels.map { this.testKubePodService.findPodsByLabel(it) }.flatten()
    }

    override fun requestPort(pod: KubePod, requestedPort: Int): Int {
        checkPortAlreadyUsedByPod(pod, requestedPort)
        val vacantPort = generateVacantPort()
        this.podBinds.add(PodBind(pod, requestedPort, vacantPort))
        return vacantPort
    }

    private fun checkPortAlreadyUsedByPod(pod: KubePod, requestedPort: Int) {
        if (this.podBinds.any { it.pod == pod && it.requestedPort == requestedPort })
            throw BindException()
    }

    private fun generateVacantPort(): Int {
        var port = 1600
        while (isPortInUse(port)) {
            port++
        }
        return port
    }

    private fun isPortInUse(port: Int): Boolean {
        return this.podBinds.any { it.actualPort == port }
    }

    private fun checkAlreadyExist(name: String) {
        if (this.services.any { it.getName() == name })
            throw KubeNetworkService.ServiceAlreadyExistException()
    }

    internal fun delete(kubeService: KubeService) {
        this.services.remove(kubeService)
    }

    data class PodBind(
        val pod: KubePod,
        val requestedPort: Int,
        val actualPort: Int
    )

}
