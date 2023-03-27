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
import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.utils.CloudState
import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeClaim
import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeSpec
import app.simplecloud.simplecloud.module.api.internal.ftp.FtpServer
import app.simplecloud.simplecloud.module.api.internal.ftp.configuration.FtpCreateConfiguration
import app.simplecloud.simplecloud.module.api.internal.ftp.configuration.FtpServerConfiguration
import app.simplecloud.simplecloud.module.api.internal.request.ftp.FtpServerStopRequest
import app.simplecloud.simplecloud.module.api.internal.service.InternalFtpServerService
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersionRequestPrePostProcessor
import app.simplecloud.simplecloud.node.api.NodeAPIBaseTest
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

    private lateinit var ftpServerService: InternalFtpServerService

    @BeforeEach
    override fun setUp() {
        super.setUp()
        this.ftpServerService = this.cloudAPI.getFtpService()
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
    }

    @Test
    fun startFtpServerForNonExistingVolumeClaim_willFail() {
        Assertions.assertThrows(ResourceVersionRequestPrePostProcessor.ConstraintViolationException::class.java) {
            runBlocking {
                ftpServerService.createCreateRequest(
                    FtpCreateConfiguration(
                        "my-server",
                        "cloud",
                        "password",
                        30200,
                        NotExistingVolumeClaim()
                    )
                ).submit().await()
            }
        }
    }

    class NotExistingVolumeClaim : KubeVolumeClaim {

        override fun getName(): String {
            return "not-existing-claim"
        }

        override fun delete() {
        }

    }

    @Test
    fun startFtpServerWithInvalidPort_willFail() {
        val volumeClaim = givenKubeVolumeClaim("my-claim")
        Assertions.assertThrows(ResourceVersionRequestPrePostProcessor.ConstraintViolationException::class.java) {
            runBlocking {
                ftpServerService.createCreateRequest(
                    FtpCreateConfiguration(
                        "my-server",
                        "cloud",
                        "password",
                        30,
                        volumeClaim
                    )
                ).submit().await()
            }
        }
    }

    @Test
    fun startFtpServerWith_TooShortUsername_willFail() {
        val volumeClaim = givenKubeVolumeClaim("my-claim")
        Assertions.assertThrows(ResourceVersionRequestPrePostProcessor.ConstraintViolationException::class.java) {
            runBlocking {
                ftpServerService.createCreateRequest(
                    FtpCreateConfiguration(
                        "my-server",
                        "cl",
                        "password",
                        30200,
                        volumeClaim
                    )
                ).submit().await()
            }
        }
    }

    @Test
    fun startFtpServerWith_TooShortPassword_willFail() {
        val volumeClaim = givenKubeVolumeClaim("my-claim")
        Assertions.assertThrows(ResourceVersionRequestPrePostProcessor.ConstraintViolationException::class.java) {
            runBlocking {
                ftpServerService.createCreateRequest(
                    FtpCreateConfiguration(
                        "my-server",
                        "cloud",
                        "pa",
                        30200,
                        volumeClaim
                    )
                ).submit().await()
            }
        }
    }

    private fun givenKubeVolumeClaim(name: String): KubeVolumeClaim {
        try {
            return this.kubeAPI.getVolumeClaimService().getClaim(name)
        } catch (e: NoSuchElementException) {
            return this.kubeAPI.getVolumeClaimService().createVolumeClaim(
                name,
                KubeVolumeSpec().withRequestedStorageInGB(1)
            )
        }

    }

    @Test
    fun startFtpServerForExistingVolumeClaim_podAndServiceWillBeCreated() {
        val ftpServer = startFtpServer()
        assertFtpServerByName("ftp-server-${ftpServer.getName()}")
        assertFtpService("ftp-service-${ftpServer.getName()}")
    }

    private fun startFtpServer(): FtpServer = runBlocking {
        val volumeClaim = givenKubeVolumeClaim("my-claim")
        return@runBlocking ftpServerService.createCreateRequest(
            FtpCreateConfiguration(
                "my-ftp-server",
                "cloud",
                "password",
                30200,
                volumeClaim
            )
        ).submit().await()
    }

    private fun stopFtpServer(ftpServer: FtpServer) {
        runBlocking {
            ftpServerService.createStopRequest(ftpServer)
                .submit().await()
        }
    }

    @Test
    fun startAndStopFtpServerForExistingVolumeClaim_podAndServiceWillBeDeleted() {
        val ftpServer = startFtpServer()
        stopFtpServer(ftpServer)
        assertNotFtpServerByName("ftp-server-${ftpServer.getName()}")
        assertNotFtpService("ftp-service-${ftpServer.getName()}")
    }

    @Test
    fun startFtpServerTwice_secondStartWillFail() {
        val ftpServer = startFtpServer()
        stopFtpServer(ftpServer)
        startFtpServer()
    }

    @Test
    fun stopNotStartedFtpServer_willFail() {
        Assertions.assertThrows(NoSuchElementException::class.java) {
            stopFtpServer(NotStartedFtpServer(this.ftpServerService))
        }
    }

    class NotStartedFtpServer(
        private val service: InternalFtpServerService,
    ) : FtpServer {
        override fun createStopRequest(): FtpServerStopRequest {
            return this.service.createStopRequest(this)
        }

        override fun toConfiguration(): FtpServerConfiguration {
            return FtpServerConfiguration(
                "FtpServer-1",
                "cloud",
                "passowrd",
                "my-claim",
                30200
            )
        }

    }

    @Test
    fun startFtpServerWhenCloudDisabled_willFail() {
        this.cloudAPI.getCloudStateService().setCloudState(CloudState.DISABLED)
        Assertions.assertThrows(IllegalStateException::class.java) {
            startFtpServer()
        }
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