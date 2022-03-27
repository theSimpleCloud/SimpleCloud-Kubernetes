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

import app.simplecloud.simplecloud.kubernetes.api.container.Container
import app.simplecloud.simplecloud.kubernetes.api.secret.KubeSecretService
import app.simplecloud.simplecloud.kubernetes.api.service.KubeService
import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeClaim
import app.simplecloud.simplecloud.kubernetes.impl.container.KubernetesContainer
import app.simplecloud.simplecloud.kubernetes.impl.secret.KubeSecretServiceImpl
import app.simplecloud.simplecloud.kubernetes.impl.service.KubeServiceImpl
import app.simplecloud.simplecloud.kubernetes.impl.volume.KubeVolumeClaimImpl
import com.google.inject.AbstractModule
import com.google.inject.assistedinject.FactoryModuleBuilder
import io.kubernetes.client.openapi.Configuration
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.util.Config


class KubernetesBinderModule : AbstractModule() {

    override fun configure() {
        Configuration.setDefaultApiClient(Config.defaultClient())
        val api = CoreV1Api()
        bind(CoreV1Api::class.java).toInstance(api)

        bind(KubeSecretService::class.java).to(KubeSecretServiceImpl::class.java)

        install(
            FactoryModuleBuilder()
                .implement(KubeService::class.java, KubeServiceImpl::class.java)
                .build(KubeService.Factory::class.java)
        )

        install(
            FactoryModuleBuilder()
                .implement(Container::class.java, KubernetesContainer::class.java)
                .build(Container.Factory::class.java)
        )

        install(
            FactoryModuleBuilder()
                .implement(KubeVolumeClaim::class.java, KubeVolumeClaimImpl::class.java)
                .build(KubeVolumeClaim.Factory::class.java)
        )
    }

}