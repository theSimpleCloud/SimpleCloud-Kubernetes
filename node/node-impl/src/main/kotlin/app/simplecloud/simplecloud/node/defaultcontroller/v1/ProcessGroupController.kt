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

import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.template.configuration.AbstractProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.configuration.LobbyProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.configuration.ProxyProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.configuration.ServerProcessTemplateConfiguration
import app.simplecloud.simplecloud.node.defaultcontroller.v1.handler.ProcessGroupUpdateHandler
import app.simplecloud.simplecloud.restserver.api.controller.Controller
import app.simplecloud.simplecloud.restserver.api.controller.annotation.RequestBody
import app.simplecloud.simplecloud.restserver.api.controller.annotation.RequestMapping
import app.simplecloud.simplecloud.restserver.api.controller.annotation.RequestPathParam
import app.simplecloud.simplecloud.restserver.api.controller.annotation.RestController
import app.simplecloud.simplecloud.restserver.api.route.RequestType
import kotlinx.coroutines.runBlocking

/**
 * Created by IntelliJ IDEA.
 * Date: 28.06.2021
 * Time: 12:43
 * @author Frederick Baier
 */
@RestController(1, "cloud/group")
class ProcessGroupController(
    private val groupService: CloudProcessGroupService,
) : Controller {


    private val groupUpdateHandler = ProcessGroupUpdateHandler(this.groupService)

    @RequestMapping(RequestType.GET, "", "web.cloud.group.get")
    fun handleGroupGetAll(): List<AbstractProcessTemplateConfiguration> {
        val groups = this.groupService.findAll().join()
        return groups.map { it.toConfiguration() }
    }

    @RequestMapping(RequestType.GET, "{name}", "web.cloud.group.get")
    fun handleGroupGetOne(@RequestPathParam("name") name: String): AbstractProcessTemplateConfiguration {
        val group = this.groupService.findByName(name).join()
        return group.toConfiguration()
    }

    @RequestMapping(RequestType.POST, "", "web.cloud.group.create")
    fun handleGroupCreate(
        @RequestBody(
            types = [
                "LOBBY",
                "PROXY",
                "SERVER"
            ],
            classes = [
                LobbyProcessTemplateConfiguration::class,
                ProxyProcessTemplateConfiguration::class,
                ServerProcessTemplateConfiguration::class
            ]
        ) configuration: AbstractProcessTemplateConfiguration,
    ): AbstractProcessTemplateConfiguration {
        val completableFuture = this.groupService.createCreateRequest(configuration).submit()
        val group = completableFuture.join()
        return group.toConfiguration()
    }

    @RequestMapping(RequestType.PUT, "", "web.cloud.group.update")
    fun handleGroupUpdate(
        @RequestBody(
            types = [
                "LOBBY",
                "PROXY",
                "SERVER"
            ],
            classes = [
                LobbyProcessTemplateConfiguration::class,
                ProxyProcessTemplateConfiguration::class,
                ServerProcessTemplateConfiguration::class
            ]
        ) configuration: AbstractProcessTemplateConfiguration,
    ): Boolean = runBlocking {
        groupUpdateHandler.update(configuration)
        return@runBlocking true
    }

    @RequestMapping(RequestType.DELETE, "{name}", "we.cloud.group.delete")
    fun handleDelete(@RequestPathParam("name") groupName: String): Boolean {
        val group = this.groupService.findByName(groupName).join()
        val groupDeleteRequest = group.createDeleteRequest()
        groupDeleteRequest.submit().join()
        return true
    }


}