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

package app.simplecloud.simplecloud.kubernetes.test.secret

import app.simplecloud.simplecloud.kubernetes.api.secret.KubeSecret
import app.simplecloud.simplecloud.kubernetes.api.secret.KubeSecretService
import app.simplecloud.simplecloud.kubernetes.api.secret.SecretSpec
import java.util.concurrent.CopyOnWriteArrayList

class TestKubeSecretService : KubeSecretService {

    private val secrets = CopyOnWriteArrayList<KubeSecret>()

    override fun createSecret(name: String, secretSpec: SecretSpec): KubeSecret {
        checkAlreadyExist(name.lowercase())
        val secret = TestKubeSecret(name.lowercase(), secretSpec)
        this.secrets.add(secret)
        return secret
    }

    private fun checkAlreadyExist(name: String) {
        if (this.secrets.any { it.getName() == name }) {
            throw KubeSecretService.SecretAlreadyExistException()
        }
    }

    override fun getSecret(name: String): KubeSecret {
        return this.secrets.first { it.getName() == name.lowercase() }
    }

}
