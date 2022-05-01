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

package app.simplecloud.simplecloud.node.startup.prepare

import app.simplecloud.simplecloud.kubernetes.api.KubeAPI
import app.simplecloud.simplecloud.kubernetes.api.pod.KubePodService
import app.simplecloud.simplecloud.kubernetes.api.secret.KubeSecretService
import app.simplecloud.simplecloud.kubernetes.api.service.KubeNetworkService
import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeClaimService
import com.google.inject.AbstractModule

/**
 * Date: 30.04.22
 * Time: 18:50
 * @author Frederick Baier
 *
 */
class KubeBinderModule(
    private val kubeAPI: KubeAPI
) : AbstractModule() {

    override fun configure() {
        bind(KubeAPI::class.java).toInstance(this.kubeAPI)

        bind(KubePodService::class.java).toInstance(this.kubeAPI.getPodService())
        bind(KubeSecretService::class.java).toInstance(this.kubeAPI.getSecretService())
        bind(KubeNetworkService::class.java).toInstance(this.kubeAPI.getNetworkService())
        bind(KubeVolumeClaimService::class.java).toInstance(this.kubeAPI.getVolumeClaimService())
    }

}