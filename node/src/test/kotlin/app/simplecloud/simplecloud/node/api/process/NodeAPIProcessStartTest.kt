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

package app.simplecloud.simplecloud.node.api.process

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.impl.image.ImageImpl
import kotlinx.coroutines.runBlocking
import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * Date: 12.05.22
 * Time: 14:05
 * @author Frederick Baier
 *
 */
class NodeAPIProcessStartTest : NodeAPIProcessTest() {

    @Test
    fun startNothing_ZeroProcessesWillExist() {
        assertProcessesCount(0)
    }

    @Test
    fun startProcess_OneProcessWilExist() {
        this.processService.createStartRequest(this.defaultGroup).submit().join()
        assertProcessesCount(1)
    }

    @Test
    fun startProcess_RegisteredProcessAndReturnedProcessWillBeEqual() {
        val returnedProcess = this.processService.createStartRequest(this.defaultGroup).submit().join()
        val registeredProcess = this.processService.findAll().join().first()
        assertEquals(returnedProcess.toConfiguration(), registeredProcess.toConfiguration())
    }

    @Test
    fun startTwoProcesses_twoProcessesWillExist() {
        this.processService.createStartRequest(this.defaultGroup).submit().join()
        this.processService.createStartRequest(this.defaultGroup).submit().join()
        assertProcessesCount(2)
    }

    @Test
    fun startProcessWithCustomProcessNumber() {
        val process = this.processService.createStartRequest(this.defaultGroup)
            .setProcessNumber(14)
            .submit().join()
        assertEquals(14, process.getProcessNumber())
    }

    @Test
    fun startProcessWithTooLowProcessNumber_willFail() {
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                processService.createStartRequest(defaultGroup)
                    .setProcessNumber(0)
                    .submit().await()
            }
        }
    }

    @Test
    fun startWithSameProcessNumberTwice_SecondWillFail() {
        processService.createStartRequest(defaultGroup)
            .setProcessNumber(5)
            .submit().join()
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                processService.createStartRequest(defaultGroup)
                    .setProcessNumber(5)
                    .submit().await()
            }
        }
    }

    @Test
    fun startProcessWithCustomMaxMemory() {
        val customMemory = 3423
        val process = this.processService.createStartRequest(this.defaultGroup)
            .setMaxMemory(customMemory)
            .submit().join()
        assertEquals(customMemory, process.getMaxMemory())
    }

    @Test
    fun startProcessWithTooLowMaxMemory_willFail() {
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                processService.createStartRequest(defaultGroup)
                    .setMaxMemory(200)
                    .submit().await()
            }
        }
    }

    @Test
    fun startProcessWithCustomImage() {
        val imageName = RandomStringUtils.randomAlphabetic(16)
        val process = this.processService.createStartRequest(this.defaultGroup)
            .setImage(ImageImpl.fromName(imageName)!!)
            .submit().join()
        assertEquals(imageName, process.getImage().getName())
    }

    @Test
    fun startProcessWithCustomMaxPlayers() {
        val process = this.processService.createStartRequest(this.defaultGroup)
            .setMaxPlayers(24)
            .submit().join()
        assertEquals(24, process.getMaxPlayers())
    }

    @Test
    fun startProcessWithTooLowMaxPlayers_willFail() {
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                processService.createStartRequest(defaultGroup)
                    .setMaxPlayers(-5)
                    .submit().await()
            }
        }
    }

    @Test
    fun startProcessWithTooLowMaxPlayers2_willFail() {
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                processService.createStartRequest(defaultGroup)
                    .setMaxPlayers(-2)
                    .submit().await()
            }
        }
    }

    @Test
    fun startProcessWitInfiniteMaxPlayers() {
        processService.createStartRequest(defaultGroup)
            .setMaxPlayers(-1)
            .submit().join()
    }

    @Test
    fun startProcessKubernetesTest() {
        val process = processService.createStartRequest(defaultGroup)
            .submit().join()
        assertProcessWasCreatedInKubernetes(process.getName())
    }

    private fun assertProcessWasCreatedInKubernetes(name: String) {
        assertDoesNotThrow {
            this.kubeAPI.getPodService().getPod(name)
        }
    }

}