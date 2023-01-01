package app.simplecloud.simplecloud.kubernetes.impl.service

import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.kubernetes.api.exception.KubeException
import app.simplecloud.simplecloud.kubernetes.api.pod.KubePod
import app.simplecloud.simplecloud.kubernetes.api.service.KubeNetworkService
import app.simplecloud.simplecloud.kubernetes.api.service.KubeService
import app.simplecloud.simplecloud.kubernetes.api.service.ServiceSpec
import io.kubernetes.client.custom.IntOrString
import io.kubernetes.client.openapi.ApiException
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1ObjectMeta
import io.kubernetes.client.openapi.models.V1Service
import io.kubernetes.client.openapi.models.V1ServicePort
import io.kubernetes.client.openapi.models.V1ServiceSpec

/**
 * Date: 30.04.22
 * Time: 13:18
 * @author Frederick Baier
 *
 */
class KubeNetworkServiceImpl(
    private val api: CoreV1Api
) : KubeNetworkService {

    override fun createService(name: String, serviceSpec: ServiceSpec): KubeService {
        val service = createServiceObj(name.lowercase(), serviceSpec)
        try {
            this.api.createNamespacedService("default", service, null, null, null, null)
        } catch (ex: ApiException) {
            throw KubeException(ex.responseBody, ex)
        }
        return getService(name.lowercase())
    }

    private fun createServiceObj(name: String, serviceSpec: ServiceSpec): V1Service {
        val labels = serviceSpec.labels.associate { it.getNamePair() }
        val port = createServicePort(serviceSpec)
        val type = generateType(serviceSpec)
        return V1Service()
            .metadata(V1ObjectMeta().name(name))
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

    private fun createServicePort(serviceSpec: ServiceSpec): V1ServicePort {
        val port = V1ServicePort()
            .protocol("TCP")
            .port(serviceSpec.clusterPort)
            .targetPort(IntOrString(serviceSpec.containerPort))

        if (serviceSpec.publicPort != -1)
            port.nodePort(serviceSpec.publicPort)
        return port
    }

    private fun generateType(serviceSpec: ServiceSpec): String {
        if (serviceSpec.publicPort == -1) {
            return "ClusterIP"
        }
        return "NodePort"
    }

    override fun getService(name: String): KubeService {
        try {
            return KubeServiceImpl(name.lowercase(), this.api)
        } catch (e: KubeException) {
            throw NoSuchElementException("Service '${name}' does not exist")
        }
    }

    override fun translateAddress(address: Address): Address {
        return address
    }

    override fun requestPort(pod: KubePod, port: Int): Int {
        return port
    }
}