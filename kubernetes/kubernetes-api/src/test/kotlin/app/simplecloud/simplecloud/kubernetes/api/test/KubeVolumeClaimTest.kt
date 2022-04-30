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

import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeClaimService
import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeSpec
import app.simplecloud.simplecloud.kubernetes.api.volumeclaim.TestKubeVolumeClaimService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Date: 24.04.22
 * Time: 13:31
 * @author Frederick Baier
 *
 */
class KubeVolumeClaimTest {


    private var kubeVolumeClaimService: KubeVolumeClaimService = TestKubeVolumeClaimService()

    @BeforeEach
    internal fun setUp() {
        this.kubeVolumeClaimService = TestKubeVolumeClaimService()
    }

    @Test
    internal fun newVolumeClaimService_ClaimsWillBeEmpty() {
        Assertions.assertEquals(0, kubeVolumeClaimService.getAllClaims().size)
    }

    @Test
    internal fun afterCreateOneClaim_ClaimsWillBeOne() {
        kubeVolumeClaimService.createVolumeClaim("test", KubeVolumeSpec())
        Assertions.assertEquals(1, kubeVolumeClaimService.getAllClaims().size)
    }

    @Test
    internal fun afterCreateOneAndDelete_ClaimsWillBeEmpty() {
        val volumeClaim = kubeVolumeClaimService.createVolumeClaim("test", KubeVolumeSpec())
        volumeClaim.delete()
        Assertions.assertEquals(0, kubeVolumeClaimService.getAllClaims().size)
    }

    @Test
    internal fun afterCreateTwo_ClaimsWillBeTwo() {
        kubeVolumeClaimService.createVolumeClaim("test", KubeVolumeSpec())
        kubeVolumeClaimService.createVolumeClaim("test2", KubeVolumeSpec())
        Assertions.assertEquals(2, kubeVolumeClaimService.getAllClaims().size)
    }

    @Test
    internal fun createClaimWithSameNameTwice_WillThrowError() {
        kubeVolumeClaimService.createVolumeClaim("test", KubeVolumeSpec())
        Assertions.assertThrows(KubeVolumeClaimService.VolumeClaimAlreadyExistException::class.java) {
            kubeVolumeClaimService.createVolumeClaim("test", KubeVolumeSpec())
        }
    }

    @Test
    internal fun getNotExistingClaim_WillThrowError() {
        Assertions.assertThrows(NoSuchElementException::class.java) {
            kubeVolumeClaimService.getClaim("test")
        }
    }

    @Test
    internal fun afterCreate_GetWillNotThrow() {
        kubeVolumeClaimService.createVolumeClaim("test", KubeVolumeSpec())
        kubeVolumeClaimService.getClaim("test")
    }

}