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

import app.simplecloud.simplecloud.api.internal.messagechannel.InternalMessageChannelProvider
import app.simplecloud.simplecloud.module.api.internal.service.InternalNodeCloudAPI
import app.simplecloud.simplecloud.node.update.NodeRestarter
import app.simplecloud.simplecloud.restserver.api.controller.Controller
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
@RestController(1, "cloud/restart")
class RestartController(
    private val cloudAPI: InternalNodeCloudAPI,
    private val messageChannelProvider: InternalMessageChannelProvider,
) : Controller {

    @RequestMapping(RequestType.POST, "", "web.cloud.process.restart")
    fun handleRestart(): Boolean {
        val nodeRestarter = NodeRestarter(this.cloudAPI, this.messageChannelProvider)
        if (!nodeRestarter.canPerformRestart()) {
            throw UnableToRestartException()
        }
        thread {
            try {
                nodeRestarter.restartNodes()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return true
    }

    class UnableToRestartException() : Exception("Unable to restart")

}