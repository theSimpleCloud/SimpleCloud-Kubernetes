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
import io.kubernetes.client.openapi.apis.CoreV1Api
import java.nio.charset.StandardCharsets

class KubeSecretImpl(
    name: String,
    private val api: CoreV1Api
) : KubeSecret {

    private val name: String = name.lowercase()

    private val secret = api.readNamespacedSecret(this.name, "default", null)

    override fun getStringValueOf(key: String): String {
        val bytes = secret.data?.get(key)!!
        return String(bytes, StandardCharsets.UTF_8)
    }

    override fun getName(): String {
        return this.name
    }


}