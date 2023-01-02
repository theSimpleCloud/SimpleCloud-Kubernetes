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

package app.simplecloud.simplecloud.kubernetes.test

import app.simplecloud.simplecloud.kubernetes.test.deployment.TestKubeDeploymentService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

/**
 * Date: 02.01.23
 * Time: 15:26
 * @author Frederick Baier
 *
 */
class KubeDeploymentTest {

    private val kubeDeploymentService = TestKubeDeploymentService()

    @Test
    fun simplecloudDeployment_alwaysExists() {
        this.kubeDeploymentService.getDeployment("simplecloud")
    }

    @Test
    fun getNotExistingDeployment_wilFail() {
        Assertions.assertThrows(NoSuchElementException::class.java) {
            this.kubeDeploymentService.getDeployment("deployment")
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["image1", "image2", "image3"])
    fun editImageTest(imageName: String) {
        val deployment = this.kubeDeploymentService.getDeployment("simplecloud")
        deployment.editImage(imageName)
        Assertions.assertEquals(imageName, deployment.getImageName())
    }

}