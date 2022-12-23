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

package app.simplecloud.simplecloud.node.defaultcontroller.v1

import app.simplecloud.simplecloud.api.service.StaticProcessTemplateService
import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeClaimService
import app.simplecloud.simplecloud.module.api.internal.service.InternalFtpServerService
import app.simplecloud.simplecloud.node.defaultcontroller.v1.dto.VolumeDto
import app.simplecloud.simplecloud.node.defaultcontroller.v1.dto.VolumeFtpServerStartDto
import app.simplecloud.simplecloud.node.defaultcontroller.v1.handler.StaticProcessVolumeHandler
import app.simplecloud.simplecloud.restserver.api.controller.Controller
import app.simplecloud.simplecloud.restserver.api.controller.annotation.RequestBody
import app.simplecloud.simplecloud.restserver.api.controller.annotation.RequestMapping
import app.simplecloud.simplecloud.restserver.api.controller.annotation.RestController
import app.simplecloud.simplecloud.restserver.api.route.RequestType
import kotlinx.coroutines.runBlocking

/**
 * Date: 22.12.22
 * Time: 17:23
 * @author Frederick Baier
 *
 */
@RestController(1, "cloud/volume")
class VolumeController(
    private val ftpServerService: InternalFtpServerService,
    private val kubeVolumeClaimService: KubeVolumeClaimService,
    private val staticTemplateService: StaticProcessTemplateService,
) : Controller {

    private val volumeHandler =
        StaticProcessVolumeHandler(ftpServerService, kubeVolumeClaimService, staticTemplateService)

    @RequestMapping(RequestType.GET, "", "web.cloud.volume.get")
    fun handleGetAll(): List<VolumeDto> {
        return runBlocking {
            return@runBlocking volumeHandler.getAllAvailableVolumes()
        }
    }

    @RequestMapping(RequestType.POST, "start", "web.cloud.volume.start")
    fun handleStart(@RequestBody body: VolumeFtpServerStartDto): VolumeDto {
        return runBlocking {
            return@runBlocking volumeHandler.startFtpServer(body.staticProcessName)
        }
    }

    @RequestMapping(RequestType.POST, "stop", "web.cloud.volume.stop")
    fun handleStop(@RequestBody body: VolumeFtpServerStartDto): Boolean {
        return runBlocking {
            volumeHandler.stopFtpServer(body.staticProcessName)
            return@runBlocking true
        }
    }

}