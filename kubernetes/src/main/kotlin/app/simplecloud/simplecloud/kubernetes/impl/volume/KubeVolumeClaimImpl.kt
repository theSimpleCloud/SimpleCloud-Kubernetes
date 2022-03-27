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
import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeSpec
import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import io.kubernetes.client.custom.Quantity
import io.kubernetes.client.openapi.ApiException
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1ObjectMeta
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaimSpec
import io.kubernetes.client.openapi.models.V1ResourceRequirements

class KubeVolumeClaimImpl @Inject constructor(
    @Assisted name: String,
    @Assisted private val volumeSpec: KubeVolumeSpec,
    private val api: CoreV1Api
) : KubeVolumeClaim {

    private val name: String = name.lowercase()

    init {
        if (!doesVolumeClaimExist()) {
            claimVolume()
        }
    }

    private fun doesVolumeClaimExist(): Boolean {
        return runCatching {
            this.api.readNamespacedPersistentVolumeClaim(this.name, "default", null)
        }.isSuccess
    }

    private fun claimVolume() {
        val persistentVolumeClaim = createPersistentVolumeClaimObj()

        try {
            this.api.createNamespacedPersistentVolumeClaim(
                "default",
                persistentVolumeClaim,
                null,
                null,
                null
            )
        } catch (e: ApiException) {
            //If the request fails, the volume already exists
        }
    }

    private fun createPersistentVolumeClaimObj() = V1PersistentVolumeClaim()
        .metadata(V1ObjectMeta().name(this.name))
        .spec(
            V1PersistentVolumeClaimSpec()
                .storageClassName(this.volumeSpec.storageClassName.lowercase())
                .accessModes(listOf("ReadWriteOnce"))
                .resources(
                    V1ResourceRequirements()
                        .requests(mapOf("storage" to Quantity.fromString("${this.volumeSpec.requestedStorageInGB}Gi")))
                )
        )

    override fun delete() {
        this.api.deleteNamespacedPersistentVolumeClaim(
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

    override fun getName(): String {
        return this.name
    }

}