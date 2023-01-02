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

package app.simplecloud.simplecloud.node.defaultcontroller.v1.handler

import app.simplecloud.simplecloud.api.CloudAPI
import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.service.StaticProcessTemplateService
import app.simplecloud.simplecloud.api.template.static.StaticProcessTemplate
import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeClaim
import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeClaimService
import app.simplecloud.simplecloud.module.api.internal.ftp.FtpServer
import app.simplecloud.simplecloud.module.api.internal.ftp.configuration.FtpCreateConfiguration
import app.simplecloud.simplecloud.module.api.internal.service.InternalFtpServerService
import app.simplecloud.simplecloud.node.defaultcontroller.v1.dto.VolumeDto
import org.apache.commons.lang3.RandomStringUtils

/**
 * Date: 22.12.22
 * Time: 17:23
 * @author Frederick Baier
 *
 */
class StaticProcessVolumeHandler(
    private val cloudAPI: CloudAPI,
    private val ftpServerService: InternalFtpServerService,
    private val kubeVolumeClaimService: KubeVolumeClaimService,
    private val staticTemplateService: StaticProcessTemplateService,
) {

    suspend fun stopFtpServer(staticProcessName: String) {
        val ftpServerName = createFtpServerNameFromTemplateName(staticProcessName)
        if (!doesFtpServerExist(ftpServerName)) {
            throw FtpServerException("Ftp server is not running")
        }
        val ftpServer = this.ftpServerService.findByName(ftpServerName).await()
        ftpServer.createStopRequest().submit().await()
    }

    suspend fun startFtpServer(staticProcessName: String): VolumeDto {
        checkCloudDisabled()
        val volumeClaim = getVolumeClaimForStaticProcess(staticProcessName)
        val ftpServerName = createFtpServerNameFromTemplateName(staticProcessName)
        if (doesFtpServerExist(ftpServerName))
            throw FtpServerException("Ftp Server is already running")
        val future = ftpServerService.createCreateRequest(
            FtpCreateConfiguration(
                ftpServerName,
                "cloud",
                RandomStringUtils.randomAlphanumeric(32),
                volumeClaim
            )
        ).submit()
        val ftpServer = future.await()
        return VolumeDto(
            staticProcessName,
            true,
            ftpServer.getFtpUser(),
            ftpServer.getFtpPassword(),
            ftpServer.getPort()
        )
    }

    private fun getVolumeClaimForStaticProcess(staticProcessName: String): KubeVolumeClaim {
        val claimName = createVolumeClaimNameFromTemplateName(staticProcessName)
        if (!doesVolumeClaimExist(claimName))
            throw FtpServerException("Requested volume does not exist")
        return kubeVolumeClaimService.getClaim(claimName)
    }

    private suspend fun checkCloudDisabled() {
        if (cloudAPI.isDisabledMode().await()) {
            throw FtpServerException("Unable to perform action. Cloud is disabled.")
        }
    }


    private suspend fun doesFtpServerExist(ftpServerName: String): Boolean {
        try {
            this.ftpServerService.findByName(ftpServerName).await()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun getAllAvailableVolumes(): List<VolumeDto> {
        val staticTemplates = getStaticTemplates()
        return staticTemplates.mapNotNull { createVolumeDtoFromStaticTemplate(it) }
    }

    private suspend fun createVolumeDtoFromStaticTemplate(processTemplate: StaticProcessTemplate): VolumeDto? {
        val volumeClaimName = createVolumeClaimNameFromTemplateName(processTemplate.getName())
        if (!doesVolumeClaimExist(volumeClaimName))
            return null
        val ftpServer = getFtpServerOrNull(processTemplate)
        if (ftpServer == null) {
            return VolumeDto(processTemplate.getName(), false, "", "", -1)
        }
        return VolumeDto(
            processTemplate.getName(),
            true,
            ftpServer.getFtpUser(),
            ftpServer.getFtpPassword(),
            ftpServer.getPort()
        )
    }

    private suspend fun getFtpServerOrNull(processTemplate: StaticProcessTemplate): FtpServer? {
        val ftpServerName = createFtpServerNameFromTemplateName(processTemplate.getName())
        try {
            return ftpServerService.findByName(ftpServerName).await()
        } catch (e: Exception) {
            return null
        }
    }

    private fun doesVolumeClaimExist(claimName: String): Boolean {
        try {
            this.kubeVolumeClaimService.getClaim(claimName)
            return true
        } catch (e: NoSuchElementException) {
            return false
        }
    }

    private suspend fun getStaticTemplates(): List<StaticProcessTemplate> {
        return this.staticTemplateService.findAll().await()
    }

    private fun createVolumeClaimNameFromTemplateName(templateName: String): String {
        return "claim-${templateName}".lowercase()
    }

    private fun createFtpServerNameFromTemplateName(templateName: String): String {
        return "static-${templateName}".lowercase()
    }

    class FtpServerException(msg: String) : Exception(msg)

}