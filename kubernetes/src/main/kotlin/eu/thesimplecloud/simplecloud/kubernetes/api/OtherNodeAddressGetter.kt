package eu.thesimplecloud.simplecloud.kubernetes.api

import com.google.inject.Inject
import eu.thesimplecloud.simplecloud.api.utils.Address
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1Pod

class OtherNodeAddressGetter @Inject constructor(
    private val api: CoreV1Api
) {

    private val selfNodeIp = System.getenv("SELF_HOST")

    fun getOtherNodeAddresses(): List<Address> {
        val podList = api.listNamespacedPod(
            "default",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
        val simpleCloudPods = getSimpleCloudPods(podList.items)
        val podIps = simpleCloudPods.map { it.status!!.podIP!! }
        val podIpsWithoutSelfIp = podIps.filter { it != selfNodeIp }
        return podIpsWithoutSelfIp.map { Address(it, NODE_PORT) }
    }

    private fun getSimpleCloudPods(list: List<V1Pod>): List<V1Pod> {
        return list.filter { isSimpleCloudPod(it) }
    }

    private fun isSimpleCloudPod(pod: V1Pod): Boolean {
        val labels = pod.metadata?.labels ?: emptyMap()
        return labels["app"] == "simplecloud"
    }

    companion object {
        const val NODE_PORT = 1670
    }

}