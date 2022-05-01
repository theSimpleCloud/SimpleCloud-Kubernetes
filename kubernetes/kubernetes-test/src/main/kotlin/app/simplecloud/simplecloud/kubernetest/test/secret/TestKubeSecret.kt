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

package app.simplecloud.simplecloud.kubernetest.test.secret

import app.simplecloud.simplecloud.kubernetes.api.secret.KubeSecret
import app.simplecloud.simplecloud.kubernetes.api.secret.SecretSpec

class TestKubeSecret(
    private val name: String,
    private val secretSpec: SecretSpec
) : KubeSecret {
    override fun getName(): String {
        return this.name
    }

    override fun getStringValueOf(key: String): String {
        val bytes = this.secretSpec.data[key] ?: throw NoSuchElementException()
        return String(bytes, Charsets.UTF_8)
    }

}
