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

package app.simplecloud.simplecloud.node.connect

import app.simplecloud.simplecloud.kubernetes.api.secret.KubeSecretService
import app.simplecloud.simplecloud.kubernetes.api.secret.SecretSpec
import org.apache.commons.lang3.RandomStringUtils

/**
 * Date: 01.05.22
 * Time: 08:18
 * @author Frederick Baier
 *
 */
class JwtTokenLoader(
    private val kubeSecretService: KubeSecretService
) {

    fun loadJwtToken(): String {
        return try {
            this.kubeSecretService.getSecret("jwt").getStringValueOf("jwt")
        } catch (e: NoSuchElementException) {
            createJwtSecret()
        }
    }

    private fun createJwtSecret(): String {
        val secretService = this.kubeSecretService
        val secretString = RandomStringUtils.randomAlphanumeric(32)
        secretService.createSecret("jwt", SecretSpec().withData("jwt", secretString))
        return secretString
    }

}