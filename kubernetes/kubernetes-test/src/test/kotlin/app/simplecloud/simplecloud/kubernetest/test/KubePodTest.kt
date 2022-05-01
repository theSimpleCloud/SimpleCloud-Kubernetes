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

package app.simplecloud.simplecloud.kubernetest.test

import app.simplecloud.simplecloud.kubernetes.api.pod.KubePodService
import app.simplecloud.simplecloud.kubernetes.api.pod.PodSpec
import app.simplecloud.simplecloud.kubernetest.test.pod.TestKubePodService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Date: 24.04.22
 * Time: 18:36
 * @author Frederick Baier
 *
 */
class KubePodTest {

    @Test
    internal fun newPodService_get_willThrowNoSuchElement() {
        val kubePodService: KubePodService = TestKubePodService()
        Assertions.assertThrows(NoSuchElementException::class.java) {
            kubePodService.getPod("test")
        }
    }

    @Test
    internal fun afterCreate_willNotThrow() {
        val kubePodService: KubePodService = TestKubePodService()
        kubePodService.createPod("test", PodSpec())
        kubePodService.getPod("test")
    }

    @Test
    internal fun afterCreate_andShutdown_GetWillThrow() {
        val kubePodService: KubePodService = TestKubePodService()
        val kubePod = kubePodService.createPod("test", PodSpec())
        kubePod.shutdown()
        Assertions.assertThrows(NoSuchElementException::class.java) {
            kubePodService.getPod("test")
        }
    }

    @Test
    internal fun afterCreate_PodIsRunning() {
        val kubePodService: KubePodService = TestKubePodService()
        val kubePod = kubePodService.createPod("test", PodSpec())
        Assertions.assertTrue(kubePod.isRunning())
    }

    @Test
    internal fun afterCreateAndShutdown_PodIsNotRunning() {
        val kubePodService: KubePodService = TestKubePodService()
        val kubePod = kubePodService.createPod("test", PodSpec())
        kubePod.shutdown()
        Assertions.assertFalse(kubePod.isRunning())
    }


}