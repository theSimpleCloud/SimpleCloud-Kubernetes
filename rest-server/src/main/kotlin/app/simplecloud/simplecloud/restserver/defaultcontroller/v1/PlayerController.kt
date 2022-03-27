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

package app.simplecloud.simplecloud.restserver.defaultcontroller.v1

import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import app.simplecloud.simplecloud.api.service.CloudPlayerService
import app.simplecloud.simplecloud.restserver.annotation.RequestBody
import app.simplecloud.simplecloud.restserver.annotation.RequestMapping
import app.simplecloud.simplecloud.restserver.annotation.RequestPathParam
import app.simplecloud.simplecloud.restserver.annotation.RestController
import app.simplecloud.simplecloud.restserver.base.route.RequestType
import app.simplecloud.simplecloud.restserver.controller.Controller
import app.simplecloud.simplecloud.restserver.defaultcontroller.v1.dto.PlayerUpdateRequestDto
import com.google.inject.Inject
import java.util.*

/**
 * Date: 22.03.22
 * Time: 12:01
 * @author Frederick Baier
 *
 */
@RestController(1, "cloud/player")
class PlayerController @Inject constructor(
    private val playerService: CloudPlayerService,
    private val permissionFactory: Permission.Factory
) : Controller {

    @RequestMapping(RequestType.GET, "{nameOrUuid}", "web.cloud.player.get")
    fun handleGetOnePlayer(@RequestPathParam("nameOrUuid") nameOrUuid: String): OfflineCloudPlayerConfiguration {
        val player = if (isUniqueId(nameOrUuid)) {
            this.playerService.findOfflinePlayerByUniqueId(UUID.fromString(nameOrUuid)).join()
        } else {
            this.playerService.findOfflinePlayerByName(nameOrUuid).join()
        }
        return player.toConfiguration()
    }

    @RequestMapping(RequestType.PUT, "", "web.cloud.player.update")
    fun handlePlayerUpdate(
        @RequestBody(
            types = [],
            classes = [PlayerUpdateRequestDto::class]
        ) configuration: PlayerUpdateRequestDto
    ): Boolean {
        val offlineCloudPlayer = this.playerService.findOfflinePlayerByUniqueId(configuration.uniqueId).join()
        val updateRequest = offlineCloudPlayer.createUpdateRequest()
        updateRequest.setDisplayName(configuration.displayName)
        updateRequest.setWebConfig(configuration.webConfig)
        updateRequest.clearPermissions()
        val permissions = configuration.permissionPlayerConfiguration.permissions
            .map { this.permissionFactory.create(it) }
        permissions.forEach { updateRequest.addPermission(it) }
        updateRequest.submit().join()
        return true
    }

    private fun isUniqueId(string: String): Boolean {
        return runCatching { UUID.fromString(string) }.isSuccess
    }


}