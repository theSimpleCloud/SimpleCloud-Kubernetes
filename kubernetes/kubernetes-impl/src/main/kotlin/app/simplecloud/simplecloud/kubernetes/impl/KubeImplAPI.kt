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

package app.simplecloud.simplecloud.kubernetes.impl

import app.simplecloud.simplecloud.kubernetes.api.KubeAPI
import app.simplecloud.simplecloud.kubernetes.api.deployment.KubeDeploymentService
import app.simplecloud.simplecloud.kubernetes.api.pod.KubePodService
import app.simplecloud.simplecloud.kubernetes.api.secret.KubeSecretService
import app.simplecloud.simplecloud.kubernetes.api.service.KubeNetworkService
import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeClaimService
import app.simplecloud.simplecloud.kubernetes.impl.deployment.KubeDeploymentServiceImpl
import app.simplecloud.simplecloud.kubernetes.impl.pod.KubePodServiceImpl
import app.simplecloud.simplecloud.kubernetes.impl.secret.KubeSecretServiceImpl
import app.simplecloud.simplecloud.kubernetes.impl.service.KubeNetworkServiceImpl
import app.simplecloud.simplecloud.kubernetes.impl.volume.KubeVolumeClaimServiceImpl
import io.kubernetes.client.openapi.Configuration
import io.kubernetes.client.openapi.apis.AppsV1Api
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.util.Config

/**
 * Date: 30.04.22
 * Time: 15:04
 * @author Frederick Baier
 *
 */
class KubeImplAPI() : KubeAPI {

    private val coreApi = createKubernetesCoreApi()
    private val appsApi = createKubernetesAppsApi()

    private val podService = KubePodServiceImpl(this.coreApi)

    private val secretService = KubeSecretServiceImpl(this.coreApi)

    private val networkService = KubeNetworkServiceImpl(this.coreApi)

    private val volumeClaimService = KubeVolumeClaimServiceImpl(this.coreApi)

    private val kubeDeploymentService = KubeDeploymentServiceImpl(this.appsApi)

    private fun createKubernetesCoreApi(): CoreV1Api {
        Configuration.setDefaultApiClient(Config.defaultClient())
        return CoreV1Api()
    }

    private fun createKubernetesAppsApi(): AppsV1Api {
        Configuration.setDefaultApiClient(Config.defaultClient())
        return AppsV1Api()
    }

    override fun getPodService(): KubePodService {
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

    override fun getDeploymentService(): KubeDeploymentService {
        return this.kubeDeploymentService
    }
}