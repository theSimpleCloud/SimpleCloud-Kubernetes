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

package app.simplecloud.simplecloud.kubernetest.test

import app.simplecloud.simplecloud.kubernetes.api.KubeAPI
import app.simplecloud.simplecloud.kubernetes.api.secret.KubeSecretService
import app.simplecloud.simplecloud.kubernetes.api.service.KubeNetworkService
import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeClaimService
import app.simplecloud.simplecloud.kubernetest.test.pod.TestKubePodService
import app.simplecloud.simplecloud.kubernetest.test.secret.TestKubeSecretService
import app.simplecloud.simplecloud.kubernetest.test.service.TestKubeNetworkService
import app.simplecloud.simplecloud.kubernetest.test.volumeclaim.TestKubeVolumeClaimService

/**
 * Date: 30.04.22
 * Time: 15:02
 * @author Frederick Baier
 *
 */
class KubeTestAPI : KubeAPI {

    private val podService = TestKubePodService()

    private val secretService = TestKubeSecretService()

    private val networkService = TestKubeNetworkService(podService)

    private val volumeClaimService = TestKubeVolumeClaimService()

    override fun getPodService(): TestKubePodService {
        return this.podService
    }

    override fun getSecretService(): KubeSecretService {
        return this.secretService
    }

    override fun getNetworkService(): KubeNetworkService {
        return this.networkService
    }

    override fun getVolumeClaimService(): KubeVolumeClaimService {
        return this.volumeClaimService
    }
}