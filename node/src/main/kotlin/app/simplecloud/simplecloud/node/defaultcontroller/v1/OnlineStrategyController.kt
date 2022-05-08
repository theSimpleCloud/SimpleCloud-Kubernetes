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

import app.simplecloud.simplecloud.api.process.onlinestrategy.configuration.ProcessOnlineCountStrategyConfiguration
import app.simplecloud.simplecloud.api.service.NodeProcessOnlineStrategyService
import app.simplecloud.simplecloud.node.defaultcontroller.v1.dto.OnlineStrategyUpdateRequestDto
import app.simplecloud.simplecloud.restserver.api.controller.Controller
import app.simplecloud.simplecloud.restserver.api.controller.annotation.RequestBody
import app.simplecloud.simplecloud.restserver.api.controller.annotation.RequestMapping
import app.simplecloud.simplecloud.restserver.api.controller.annotation.RequestPathParam
import app.simplecloud.simplecloud.restserver.api.controller.annotation.RestController
import app.simplecloud.simplecloud.restserver.api.route.RequestType

/**
 * Date: 27.03.22
 * Time: 09:39
 * @author Frederick Baier
 *
 */
@RestController(1, "cloud/onlinestrategy")
class OnlineStrategyController(
    private val service: NodeProcessOnlineStrategyService
) : Controller {

    @RequestMapping(RequestType.GET, "", "web.cloud.onlinestrategy.get")
    fun handleGroupGetAll(): List<ProcessOnlineCountStrategyConfiguration> {
        val strategies = this.service.findAll().join()
        return strategies.map { it.toConfiguration() }
    }

    @RequestMapping(RequestType.GET, "{name}", "web.cloud.onlinestrategy.get")
    fun handleGroupGetOne(@RequestPathParam("name") name: String): ProcessOnlineCountStrategyConfiguration {
        val group = this.service.findByName(name).join()
        return group.toConfiguration()
    }

    @RequestMapping(RequestType.POST, "", "web.cloud.onlinestrategy.create")
    fun handleGroupCreate(
        @RequestBody(
            types = [],
            classes = [ProcessOnlineCountStrategyConfiguration::class]
        ) configuration: ProcessOnlineCountStrategyConfiguration
    ): ProcessOnlineCountStrategyConfiguration {
        val completableFuture = this.service.createCreateRequest(configuration).submit()
        val group = completableFuture.join()
        return group.toConfiguration()
    }

    @RequestMapping(RequestType.PUT, "", "web.cloud.onlinestrategy.update")
    fun handleGroupUpdate(
        @RequestBody(
            types = [],
            classes = [OnlineStrategyUpdateRequestDto::class]
        ) configuration: OnlineStrategyUpdateRequestDto
    ): Boolean {
        val strategy = this.service.findByName(configuration.name).join()
        val updateRequest = this.service.createUpdateRequest(strategy)
        updateRequest.clearTargetGroups()
        configuration.targetGroupNames.forEach { updateRequest.addTargetGroup(it) }
        updateRequest.setData(configuration.data)
        updateRequest.submit().join()
        return true
    }

    @RequestMapping(RequestType.DELETE, "{name}", "we.cloud.onlinestrategy.delete")
    fun handleDelete(@RequestPathParam("name") groupName: String): Boolean {
        val strategy = this.service.findByName(groupName).join()
        val groupDeleteRequest = this.service.createDeleteRequest(strategy)
        groupDeleteRequest.submit().join()
        return true
    }

}