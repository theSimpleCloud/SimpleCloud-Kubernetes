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

import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.kubernetes.api.Label
import app.simplecloud.simplecloud.kubernetes.api.pod.KubePod
import app.simplecloud.simplecloud.kubernetes.api.pod.PodSpec
import app.simplecloud.simplecloud.kubernetes.api.service.KubeNetworkService
import app.simplecloud.simplecloud.kubernetes.api.service.ServiceSpec
import app.simplecloud.simplecloud.kubernetes.test.pod.TestKubePodService
import app.simplecloud.simplecloud.kubernetes.test.service.TestKubeNetworkService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.BindException
import java.util.*

/**
 * Date: 24.04.22
 * Time: 18:36
 * @author Frederick Baier
 *
 */
class KubeNetworkTest {

    private val selfPod = SelfPod()
    private var kubePodService = TestKubePodService()
    private var kubeNetworkService: KubeNetworkService = TestKubeNetworkService(kubePodService)

    @BeforeEach
    fun setUp() {
        this.kubePodService = TestKubePodService()
        this.kubeNetworkService = TestKubeNetworkService(kubePodService)
    }

    @Test
    fun newKubeService_createService_WillNotThrow() {
        kubeNetworkService.createService("test", ServiceSpec())
    }

    @Test
    fun newKubeService_create2ServicesWithDifferentName_willNotThrow() {
        kubeNetworkService.createService("test", ServiceSpec())
        kubeNetworkService.createService("test2", ServiceSpec())
    }

    @Test
    fun newKubeService_createServiceWithSameNameTwice_willThrow() {
        kubeNetworkService.createService("test", ServiceSpec())
        assertThrows(KubeNetworkService.ServiceAlreadyExistException::class.java) {
            kubeNetworkService.createService("test", ServiceSpec())
        }
    }

    @Test
    fun newKubeService_getWillThrow() {
        assertThrows(NoSuchElementException::class.java) {
            kubeNetworkService.getService("test")
        }
    }

    @Test
    fun afterCreate_GteWillNotThrow() {
        kubeNetworkService.createService("test", ServiceSpec())
        kubeNetworkService.getService("test")
    }

    @Test
    fun requestPort_willNotThrow() {
        kubeNetworkService.requestPort(this.selfPod, 1670)
    }

    @Test
    fun requestSamePortWithSamePodTwice_willThrow() {
        kubeNetworkService.requestPort(this.selfPod, 1670)
        assertThrows(BindException::class.java) {
            kubeNetworkService.requestPort(this.selfPod, 1670)
        }
    }

    @Test
    fun requestPortWithOtherPod_willNotThrow() {
        kubeNetworkService.requestPort(this.selfPod, 1670)
        kubeNetworkService.requestPort(SelfPod(), 1670)
    }

    @Test
    fun requestForSamePortWithDifferentPods_willReturnsDifferentPorts() {
        val actualPort1 = kubeNetworkService.requestPort(this.selfPod, 1670)
        val actualPort2 = kubeNetworkService.requestPort(SelfPod(), 1670)
        assertNotEquals(actualPort1, actualPort2)
    }

    @Test
    fun translateAddress_withNotExistingService_willFail() {
        assertThrows(NoSuchElementException::class.java) {
            kubeNetworkService.translateAddress(Address("distribution", 1670))
        }
    }

    @Test
    fun translateAddress_withWrongServiceName_willFail() {
        kubeNetworkService.createService("distribution", ServiceSpec())
        assertThrows(NoSuchElementException::class.java) {
            kubeNetworkService.translateAddress(Address("abc", 1670))
        }
    }

    @Test
    fun translateAddress_withExistingServiceButNoPod_willFail() {
        kubeNetworkService.createService("distribution", ServiceSpec())
        assertThrows(KubeNetworkService.AddressTranslationException::class.java) {
            kubeNetworkService.translateAddress(Address("distribution", 1670))
        }
    }

    @Test
    fun givenExistingServiceAndPod_translateWithWrongPort_willFail() {
        val wrongPort = 7000
        val label = Label("test", "value")
        val serviceSpec = ServiceSpec().withLabels(label).withContainerPort(1670).withClusterPort(1670)
        kubeNetworkService.createService("distribution", serviceSpec)
        kubePodService.createPod("test", PodSpec().withContainerPort(1670).withLabels(label))
        assertThrows(KubeNetworkService.AddressTranslationException::class.java) {
            kubeNetworkService.translateAddress(Address("distribution", wrongPort))
        }
    }

    @Test
    fun givenExistingServiceAndPodButWrongPort_translate_willFail() {
        val wrongPort = 7000
        val label = Label("test", "value")
        val serviceSpec = ServiceSpec().withLabels(label).withContainerPort(1670).withClusterPort(1670)
        kubeNetworkService.createService("distribution", serviceSpec)
        kubePodService.createPod("test", PodSpec().withContainerPort(wrongPort).withLabels(label))
        assertThrows(KubeNetworkService.AddressTranslationException::class.java) {
            kubeNetworkService.translateAddress(Address("distribution", 1670))
        }
    }

    @Test
    fun givenExistingServiceAndPodRequestingWrongPort_translate_willFail() {
        val wrongPort = 7000
        val label = Label("test", "value")
        val serviceSpec = ServiceSpec().withLabels(label).withContainerPort(1670).withClusterPort(1670)
        kubeNetworkService.createService("distribution", serviceSpec)
        val pod = kubePodService.createPod("test", PodSpec().withContainerPort(1670).withLabels(label))
        kubeNetworkService.requestPort(pod, wrongPort)
        assertThrows(KubeNetworkService.AddressTranslationException::class.java) {
            kubeNetworkService.translateAddress(Address("distribution", 1670))
        }
    }

    @Test
    fun givenExistingServiceAndPodRequestPort_translate_willReturnActualPort() {
        val label = Label("test", "value")
        val serviceSpec = ServiceSpec().withLabels(label).withContainerPort(1670).withClusterPort(1670)
        kubeNetworkService.createService("distribution", serviceSpec)
        val pod = kubePodService.createPod("test", PodSpec().withContainerPort(1670).withLabels(label))
        val actualPort = kubeNetworkService.requestPort(pod, 1670)
        val translatedAddress = kubeNetworkService.translateAddress(Address("distribution", 1670))
        Assertions.assertEquals(actualPort, translatedAddress.port)
    }

    @Test
    fun addressTranslation_withDifferentServicePort_willNotFail() {
        val label = Label("test", "value")
        val serviceSpec = ServiceSpec().withLabels(label).withContainerPort(7000).withClusterPort(1670)
        kubeNetworkService.createService("distribution", serviceSpec)
        val pod = kubePodService.createPod("test", PodSpec().withContainerPort(1670).withLabels(label))
        kubeNetworkService.requestPort(pod, 7000)
        kubeNetworkService.translateAddress(Address("distribution", 1670))
    }

    @Test
    fun addressTranslation_withDifferentNotMatchingServicePort_willNotFail() {
        val label = Label("test", "value")
        val serviceSpec = ServiceSpec().withLabels(label).withContainerPort(7001).withClusterPort(1670)
        kubeNetworkService.createService("distribution", serviceSpec)
        val pod = kubePodService.createPod("test", PodSpec().withContainerPort(1670).withLabels(label))
        kubeNetworkService.requestPort(pod, 7000)
        assertThrows(KubeNetworkService.AddressTranslationException::class.java) {
            kubeNetworkService.translateAddress(Address("distribution", 1670))
        }
    }


    class SelfPod : KubePod {

        private val id = UUID.randomUUID()

        override fun getName(): String {
            return "self-pod-$id"
        }

        override fun execute(command: String) {
            TODO("Not yet implemented")
        }

        override fun start(podSpec: PodSpec) {
            TODO("Not yet implemented")
        }

        override fun isActive(): Boolean {
            TODO("Not yet implemented")
        }

        override fun delete() {
            TODO("Not yet implemented")
        }

        override fun forceShutdown() {
            TODO("Not yet implemented")
        }

        override fun exists(): Boolean {
            TODO("Not yet implemented")
        }

        override fun getLogs(): List<String> {
            TODO("Not yet implemented")
        }

        override fun getLabels(): List<Label> {
            return emptyList()
        }

    }

}