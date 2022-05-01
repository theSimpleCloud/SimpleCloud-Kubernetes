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

package app.simplecloud.simplecloud.kubernetest.test.volumeclaim

import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeClaim
import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeClaimService
import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeSpec
import java.util.concurrent.CopyOnWriteArrayList

class TestKubeVolumeClaimService : KubeVolumeClaimService {

    private val claims = CopyOnWriteArrayList<KubeVolumeClaim>()

    override fun createVolumeClaim(name: String, volumeSpec: KubeVolumeSpec): KubeVolumeClaim {
        checkAlreadyExist(name.lowercase())
        val kubeVolumeClaim = TestKubeVolumeClaim(name.lowercase(), volumeSpec, this)
        this.claims.add(kubeVolumeClaim)
        return kubeVolumeClaim
    }

    private fun checkAlreadyExist(name: String) {
        if (this.claims.any { it.getName() == name }) {
            throw KubeVolumeClaimService.VolumeClaimAlreadyExistException()
        }
    }

    override fun getAllClaims(): List<KubeVolumeClaim> {
        return this.claims
    }

    override fun getClaim(name: String): KubeVolumeClaim {
        return this.claims.first { it.getName() == name.lowercase() }
    }

    fun delete(volumeClaim: TestKubeVolumeClaim) {
        this.claims.remove(volumeClaim)
    }


}
