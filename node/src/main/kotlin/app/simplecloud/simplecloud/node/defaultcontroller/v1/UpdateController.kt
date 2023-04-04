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

import app.simplecloud.simplecloud.api.impl.env.EnvironmentVariables
import app.simplecloud.simplecloud.module.api.internal.service.InternalNodeCloudAPI
import app.simplecloud.simplecloud.node.defaultcontroller.v1.dto.UpdateDto
import app.simplecloud.simplecloud.node.update.NodeUpdater
import app.simplecloud.simplecloud.restserver.api.controller.Controller
import app.simplecloud.simplecloud.restserver.api.controller.annotation.RequestBody
import app.simplecloud.simplecloud.restserver.api.controller.annotation.RequestMapping
import app.simplecloud.simplecloud.restserver.api.controller.annotation.RestController
import app.simplecloud.simplecloud.restserver.api.route.RequestType
import kotlin.concurrent.thread

/**
 * Date: 27.12.22
 * Time: 20:45
 * @author Frederick Baier
 *
 */
@RestController(1, "cloud/update")
class UpdateController(
    private val cloudAPI: InternalNodeCloudAPI,
    private val environmentVariables: EnvironmentVariables,
) : Controller {

    @RequestMapping(RequestType.POST, "", "web.cloud.update")
    fun handleUpdate(@RequestBody body: UpdateDto): Boolean {
        val buildKitAddr = environmentVariables.get("BUILDKIT_ADDR")
            ?: throw NoSuchElementException("Environment variable BUILDKIT_ADDR is not set")
        val registryAddr = environmentVariables.get("REBUILD_REGISTRY")
            ?: throw NoSuchElementException("Environment variable REBUILD_REGISTRY is not set")
        val nodeUpdater = NodeUpdater(
            body.moduleLinks,
            body.baseImage,
            buildKitAddr,
            "${registryAddr}/simplecloud-internal:latest",
            this.cloudAPI
        )
        if (!nodeUpdater.canPerformUpdate()) {
            throw UnableToUpdateException()
        }
        thread {
            try {
                nodeUpdater.executeUpdate()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return true
    }

    class UnableToUpdateException() : Exception("Unable to update")

}