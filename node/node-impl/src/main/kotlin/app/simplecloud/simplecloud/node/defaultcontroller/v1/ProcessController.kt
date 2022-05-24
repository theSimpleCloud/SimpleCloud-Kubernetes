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

import app.simplecloud.simplecloud.api.internal.configutation.ProcessExecuteCommandConfiguration
import app.simplecloud.simplecloud.api.process.CloudProcessConfiguration
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.node.defaultcontroller.v1.dto.CloudProcessCreateRequestDto
import app.simplecloud.simplecloud.node.defaultcontroller.v1.handler.ProcessCreateHandler
import app.simplecloud.simplecloud.restserver.api.controller.Controller
import app.simplecloud.simplecloud.restserver.api.controller.annotation.RequestBody
import app.simplecloud.simplecloud.restserver.api.controller.annotation.RequestMapping
import app.simplecloud.simplecloud.restserver.api.controller.annotation.RequestPathParam
import app.simplecloud.simplecloud.restserver.api.controller.annotation.RestController
import app.simplecloud.simplecloud.restserver.api.route.RequestType
import kotlinx.coroutines.runBlocking

/**
 * Created by IntelliJ IDEA.
 * Date: 09/07/2021
 * Time: 19:57
 * @author Frederick Baier
 */
@RestController(1, "cloud/process")
class ProcessController(
    private val processService: CloudProcessService,
    private val groupService: CloudProcessGroupService
) : Controller {

    private val processCreateHandler = ProcessCreateHandler(this.groupService, this.processService)

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
    fun handleCreate(@RequestBody configuration: CloudProcessCreateRequestDto): CloudProcessConfiguration =
        runBlocking {
            return@runBlocking processCreateHandler.create(configuration).toConfiguration()
        }

    @RequestMapping(RequestType.DELETE, "{name}", "web.cloud.process.delete")
    fun handleShutdown(@RequestPathParam("name") name: String): Boolean {
        val process = this.processService.findByName(name).join()
        process.createShutdownRequest().submit()
        return true
    }

    @RequestMapping(RequestType.POST, "execute", "web.cloud.process.execute")
    fun handleExecuteCommand(@RequestBody configuration: ProcessExecuteCommandConfiguration): Boolean {
        val process = this.processService.findByName(configuration.processName).join()
        process.creatExecuteCommandRequest(configuration.command).submit().join()
        return true
    }

    @RequestMapping(RequestType.GET, "{name}/logs", "web.cloud.process.logs")
    fun handleGetLogs(@RequestPathParam("name") name: String): List<String> {
        val process = this.processService.findByName(name).join()
        return process.getLogs().join()
    }

}