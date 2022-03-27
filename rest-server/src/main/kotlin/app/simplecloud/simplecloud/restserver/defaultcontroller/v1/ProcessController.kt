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

import app.simplecloud.simplecloud.api.process.CloudProcessConfiguration
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.restserver.annotation.RequestBody
import app.simplecloud.simplecloud.restserver.annotation.RequestMapping
import app.simplecloud.simplecloud.restserver.annotation.RequestPathParam
import app.simplecloud.simplecloud.restserver.annotation.RestController
import app.simplecloud.simplecloud.restserver.base.route.RequestType
import app.simplecloud.simplecloud.restserver.controller.Controller
import app.simplecloud.simplecloud.restserver.defaultcontroller.v1.dto.CloudProcessCreateRequestDto
import app.simplecloud.simplecloud.restserver.defaultcontroller.v1.handler.ProcessCreateHandler
import com.google.inject.Inject
import kotlinx.coroutines.runBlocking

/**
 * Created by IntelliJ IDEA.
 * Date: 09/07/2021
 * Time: 19:57
 * @author Frederick Baier
 */
@RestController(1, "cloud/process")
class ProcessController @Inject constructor(
    private val processService: CloudProcessService,
    private val processCreateHandler: ProcessCreateHandler
) : Controller {

    @RequestMapping(RequestType.GET, "", "web.cloud.process.get")
    fun handleGetAll(): List<CloudProcessConfiguration> {
        val processes = this.processService.findAll().join()
        return processes.map { it.toConfiguration() }
    }

    @RequestMapping(RequestType.GET, "{name}", "web.cloud.process.get")
    fun handleGetOne(@RequestPathParam("name") name: String): CloudProcessConfiguration {
        val process = this.processService.findByName(name).join()
        return process.toConfiguration()
    }

    @RequestMapping(RequestType.POST, "", "web.cloud.process.create")
    fun handleCreate(@RequestBody configuration: CloudProcessCreateRequestDto): CloudProcessConfiguration = runBlocking {
        return@runBlocking processCreateHandler.create(configuration).toConfiguration()
    }

    @RequestMapping(RequestType.DELETE, "{name}", "web.cloud.process.delete")
    fun handleShutdown(@RequestPathParam("name") name: String): Boolean {
        val process = this.processService.findByName(name).join()
        process.createShutdownRequest().submit()
        return true
    }

}