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

package app.simplecloud.simplecloud.kubernetes.api

import app.simplecloud.simplecloud.distribution.api.Address
import com.google.inject.Inject
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