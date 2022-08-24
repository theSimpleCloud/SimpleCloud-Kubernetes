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

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.service.StaticProcessTemplateService
import app.simplecloud.simplecloud.api.template.configuration.AbstractProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.configuration.LobbyProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.configuration.ProxyProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.configuration.ServerProcessTemplateConfiguration
import app.simplecloud.simplecloud.node.defaultcontroller.v1.handler.ProcessTemplateUpdateHandler
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
@RestController(1, "cloud/static")
class StaticTemplateController(
    private val staticTemplateService: StaticProcessTemplateService,
) : Controller {


    @RequestMapping(RequestType.GET, "", "web.cloud.static.get")
    fun handleStaticTemplateGetAll(): List<AbstractProcessTemplateConfiguration> {
        val templates = this.staticTemplateService.findAll().join()
        return templates.map { it.toConfiguration() }
    }

    @RequestMapping(RequestType.GET, "{name}", "web.cloud.static.get")
    fun handleStaticTemplateGetOne(@RequestPathParam("name") name: String): AbstractProcessTemplateConfiguration {
        val template = this.staticTemplateService.findByName(name).join()
        return template.toConfiguration()
    }

    @RequestMapping(RequestType.POST, "", "web.cloud.static.create")
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
        val completableFuture = this.staticTemplateService.createCreateRequest(configuration).submit()
        val staticTemplate = completableFuture.join()
        return staticTemplate.toConfiguration()
    }

    @RequestMapping(RequestType.PUT, "", "web.cloud.static.update")
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
        ) targetConfig: AbstractProcessTemplateConfiguration,
    ): Boolean = runBlocking {
        val staticTemplate = staticTemplateService.findByName(targetConfig.name).await()
        ProcessTemplateUpdateHandler(staticTemplate, targetConfig).update()
        return@runBlocking true
    }

    @RequestMapping(RequestType.DELETE, "{name}", "we.cloud.static.delete")
    fun handleDelete(@RequestPathParam("name") groupName: String): Boolean {
        val staticTemplate = this.staticTemplateService.findByName(groupName).join()
        val templateDeleteRequest = staticTemplate.createDeleteRequest()
        templateDeleteRequest.submit().join()
        return true
    }


}