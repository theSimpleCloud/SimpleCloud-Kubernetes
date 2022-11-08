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

import app.simplecloud.simplecloud.module.api.error.configuration.ErrorConfiguration
import app.simplecloud.simplecloud.module.api.service.ErrorService
import app.simplecloud.simplecloud.restserver.api.controller.Controller
import app.simplecloud.simplecloud.restserver.api.controller.annotation.RequestMapping
import app.simplecloud.simplecloud.restserver.api.controller.annotation.RequestPathParam
import app.simplecloud.simplecloud.restserver.api.controller.annotation.RestController
import app.simplecloud.simplecloud.restserver.api.route.RequestType
import java.util.*

/**
 * Date: 22.10.22
 * Time: 22:28
 * @author Frederick Baier
 *
 */
@RestController(1, "cloud/error")
class ErrorController(
    private val errorService: ErrorService,
) : Controller {

    @RequestMapping(RequestType.GET, "", "web.cloud.error.get")
    fun handleGetAll(): List<ErrorConfiguration> {
        val errors = errorService.findAll().join()
        return errors.map { it.toConfiguration() }
    }

    @RequestMapping(RequestType.GET, "{uuid}", "web.cloud.error.get")
    fun handleGetOne(@RequestPathParam("uuid") uuidString: String): ErrorConfiguration {
        val uuid = UUID.fromString(uuidString)
        val error = errorService.findById(uuid).join()
        return error.toConfiguration()
    }

}