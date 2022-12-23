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

package app.simplecloud.simplecloud.node.api.ftp

import app.simplecloud.simplecloud.api.CloudAPI
import app.simplecloud.simplecloud.module.api.internal.service.InternalFtpServerService
import app.simplecloud.simplecloud.node.api.NodeAPIBaseTest
import app.simplecloud.simplecloud.node.defaultcontroller.v1.handler.StaticProcessVolumeHandler
import app.simplecloud.simplecloud.node.util.TestProcessProvider
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Date: 23.12.22
 * Time: 19:42
 * @author Frederick Baier
 *
 */
class NodeAPIFtpTest : NodeAPIBaseTest(), TestProcessProvider {

    private lateinit var staticProcessVolumeHandler: StaticProcessVolumeHandler
    private lateinit var ftpServerService: InternalFtpServerService

    @BeforeEach
    override fun setUp() {
        super.setUp()
        this.staticProcessVolumeHandler = StaticProcessVolumeHandler(
            this.cloudAPI.getFtpService(),
            this.cloudAPI.getKubeAPI().getVolumeClaimService(),
            this.cloudAPI.getStaticProcessTemplateService()
        )
        this.ftpServerService = this.cloudAPI.getFtpService()
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
    }

    @Test
    fun startFtpServerForNonExistingStaticServer_willFail() {
        Assertions.assertThrows(StaticProcessVolumeHandler.FtpServerException::class.java) {
            runBlocking {
                staticProcessVolumeHandler.startFtpServer("StaticLobby")
            }
        }
    }

    @Test
    fun startFtpServerForExistingServer_podAndServiceWillBeCreated() {
        givenStaticLobbyTemplate("StaticLobby")
        runBlocking {
            staticProcessVolumeHandler.startFtpServer("StaticLobby")
        }
        assertFtpServerByName("ftp-server-static-staticlobby")
        assertFtpService("ftp-service-static-staticlobby")
    }

    @Test
    fun startAdnStopFtpServerForExistingServer_podAndServiceWillBeDeleted() {
        givenStaticLobbyTemplate("StaticLobby")
        runBlocking {
            staticProcessVolumeHandler.startFtpServer("StaticLobby")
            staticProcessVolumeHandler.stopFtpServer("StaticLobby")
        }
        assertNotFtpServerByName("ftp-server-static-staticlobby")
        assertNotFtpService("ftp-service-static-staticlobby")
    }

    private fun assertNotFtpService(name: String) {
        Assertions.assertThrows(NoSuchElementException::class.java) {
            this.kubeAPI.getNetworkService().getService(name)
        }
    }

    private fun assertNotFtpServerByName(name: String) {
        Assertions.assertThrows(NoSuchElementException::class.java) {
            this.kubeAPI.getPodService().getPod(name)
        }
    }

    private fun assertFtpService(name: String) {
        Assertions.assertDoesNotThrow {
            this.kubeAPI.getNetworkService().getService(name)
        }
    }

    private fun assertFtpServerByName(name: String) {
        Assertions.assertDoesNotThrow {
            this.kubeAPI.getPodService().getPod(name)
        }
    }

    override fun getCloudAPI(): CloudAPI {
        return this.cloudAPI
    }

}