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

package app.simplecloud.simplecloud.kubernetes.impl.volume

import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeClaim
import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeClaimService
import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeSpec
import io.kubernetes.client.custom.Quantity
import io.kubernetes.client.openapi.ApiException
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1ObjectMeta
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaimSpec
import io.kubernetes.client.openapi.models.V1ResourceRequirements

/**
 * Date: 30.04.22
 * Time: 13:29
 * @author Frederick Baier
 *
 */
class KubeVolumeClaimServiceImpl(
    private val api: CoreV1Api
) : KubeVolumeClaimService {

    override fun getAllClaims(): List<KubeVolumeClaim> {
        val volumeClaimList = this.api.listNamespacedPersistentVolumeClaim(
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
        val items = volumeClaimList.items
        return items.map { KubeVolumeClaimImpl(it.metadata!!.name!!, this.api) }
    }

    override fun createVolumeClaim(name: String, volumeSpec: KubeVolumeSpec): KubeVolumeClaim {
        val persistentVolumeClaim = createPersistentVolumeClaimObj(name, volumeSpec)

        try {
            this.api.createNamespacedPersistentVolumeClaim(
                "default",
                persistentVolumeClaim,
                null,
                null,
                null
            )
        } catch (e: ApiException) {
            throw KubeVolumeClaimService.VolumeClaimAlreadyExistException()
        }
        return getClaim(name)
    }

    private fun createPersistentVolumeClaimObj(name: String, volumeSpec: KubeVolumeSpec) = V1PersistentVolumeClaim()
        .metadata(V1ObjectMeta().name(name))
        .spec(
            V1PersistentVolumeClaimSpec()
                .storageClassName(volumeSpec.storageClassName.lowercase())
                .accessModes(listOf("ReadWriteOnce"))
                .resources(
                    V1ResourceRequirements()
                        .requests(mapOf("storage" to Quantity.fromString("${volumeSpec.requestedStorageInGB}Gi")))
                )
        )

    override fun getClaim(name: String): KubeVolumeClaim {
        try {
            return KubeVolumeClaimImpl(name, api)
        } catch (e: ApiException) {
            throw NoSuchElementException("Volume Claim '${name}' does not exist")
        }
    }
}