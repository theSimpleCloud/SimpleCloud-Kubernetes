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

package app.simplecloud.simplecloud.kubernetes.api.test

import app.simplecloud.simplecloud.kubernetes.api.secret.KubeSecretService
import app.simplecloud.simplecloud.kubernetes.api.secret.SecretSpec
import app.simplecloud.simplecloud.kubernetes.api.secret.TestKubeSecretService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Date: 24.04.22
 * Time: 18:17
 * @author Frederick Baier
 *
 */
class KubeSecretTest {

    private var kubeSecretService: KubeSecretService = TestKubeSecretService()

    @BeforeEach
    internal fun setUp() {
        this.kubeSecretService = TestKubeSecretService()

    }

    @Test
    internal fun newKubeService_GetWillThrowError() {
        Assertions.assertThrows(NoSuchElementException::class.java) {
            kubeSecretService.getSecret("test")
        }
    }

    @Test
    internal fun afterCreate_GetWillNotThrow() {
        kubeSecretService.createSecret("test", SecretSpec().withData("test", "es"))
        kubeSecretService.getSecret("test")
    }

    @Test
    internal fun createTwiceWithDifferentName_willNotThrow() {
        kubeSecretService.createSecret("test", SecretSpec().withData("test", "es"))
        kubeSecretService.createSecret("test2", SecretSpec().withData("test", "es"))
    }

    @Test
    internal fun createTwiceWithSameName_willThrow() {
        kubeSecretService.createSecret("test", SecretSpec().withData("test", "es"))
        Assertions.assertThrows(KubeSecretService.SecretAlreadyExistException::class.java) {
            kubeSecretService.createSecret("test", SecretSpec().withData("test", "es"))
        }
    }

}