/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
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