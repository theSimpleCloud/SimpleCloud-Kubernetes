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

import app.simplecloud.simplecloud.api.node.configuration.NodeConfiguration
import app.simplecloud.simplecloud.api.service.NodeService
import app.simplecloud.simplecloud.restserver.annotation.RequestMapping
import app.simplecloud.simplecloud.restserver.annotation.RequestPathParam
import app.simplecloud.simplecloud.restserver.annotation.RestController
import app.simplecloud.simplecloud.restserver.base.route.RequestType
import app.simplecloud.simplecloud.restserver.controller.Controller
import com.google.inject.Inject
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * Date: 16/07/2021
 * Time: 11:08
 * @author Frederick Baier
 */
@RestController(1, "cloud/node")
class NodeController @Inject constructor(
    private val nodeService: NodeService
) : Controller {

    @RequestMapping(RequestType.GET, "", "web.cloud.node.get")
    fun handleGetAll(): List<NodeConfiguration> {
        val processes = this.nodeService.findAll().join()
        return processes.map { it.toConfiguration() }
    }

    @RequestMapping(RequestType.GET, "{uuid}", "web.cloud.node.get")
    fun handleGetOne(@RequestPathParam("uuid") uuid: String): NodeConfiguration {
        val process = this.nodeService.findByUniqueId(UUID.fromString(uuid)).join()
        return process.toConfiguration()
    }

}