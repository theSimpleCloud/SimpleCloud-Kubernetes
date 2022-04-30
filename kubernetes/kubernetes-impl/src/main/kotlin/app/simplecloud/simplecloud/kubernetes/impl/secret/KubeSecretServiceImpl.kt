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

package app.simplecloud.simplecloud.kubernetes.impl.secret

import app.simplecloud.simplecloud.kubernetes.api.secret.KubeSecret
import app.simplecloud.simplecloud.kubernetes.api.secret.KubeSecretService
import app.simplecloud.simplecloud.kubernetes.api.secret.SecretSpec
import io.kubernetes.client.openapi.ApiException
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1ObjectMeta
import io.kubernetes.client.openapi.models.V1Secret

/**
 * Date: 30.04.22
 * Time: 13:01
 * @author Frederick Baier
 *
 */
class KubeSecretServiceImpl(
    private val api: CoreV1Api
) : KubeSecretService {

    override fun createSecret(name: String, secretSpec: SecretSpec): KubeSecret {
        val secret = createSecretObj(name, secretSpec)
        this.api.createNamespacedSecret("default", secret, null, null, null)
        return getSecret(name)
    }

    private fun createSecretObj(
        name: String,
        secretSpec: SecretSpec
    ): V1Secret {
        return V1Secret()
            .metadata(V1ObjectMeta().name(name))
            .type("Opaque")
            .data(secretSpec.data)
    }

    override fun getSecret(name: String): KubeSecret {
        try {
            return KubeSecretImpl(name, api)
        } catch (e: ApiException) {
            throw NoSuchElementException("Kube Secret does not exist")
        }

    }
}