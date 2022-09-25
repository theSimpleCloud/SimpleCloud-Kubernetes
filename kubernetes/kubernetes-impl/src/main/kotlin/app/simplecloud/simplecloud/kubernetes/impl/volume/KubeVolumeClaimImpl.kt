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

import app.simplecloud.simplecloud.kubernetes.api.exception.KubeException
import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeClaim
import io.kubernetes.client.openapi.ApiException
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim

/**
 * Date: 30.04.22
 * Time: 13:26
 * @author Frederick Baier
 *
 */
class KubeVolumeClaimImpl(
    private val name: String,
    private val api: CoreV1Api
) : KubeVolumeClaim {

    private val claim = fetchThisVolumeClaim()

    private fun fetchThisVolumeClaim(): V1PersistentVolumeClaim {
        try {
            return this.api.readNamespacedPersistentVolumeClaim(this.name, "default", null)
        } catch (ex: ApiException) {
            throw KubeException(ex.responseBody, ex)
        }
    }

    override fun getName(): String {
        return this.name
    }

    override fun delete() {
        try {
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
        } catch (ex: ApiException) {
            throw KubeException(ex.responseBody, ex)
        }
    }
}