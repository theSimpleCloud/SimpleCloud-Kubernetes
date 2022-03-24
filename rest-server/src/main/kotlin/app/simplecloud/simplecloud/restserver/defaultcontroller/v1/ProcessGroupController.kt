/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package app.simplecloud.simplecloud.restserver.defaultcontroller.v1

import app.simplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import app.simplecloud.simplecloud.api.process.group.configuration.CloudLobbyProcessGroupConfiguration
import app.simplecloud.simplecloud.api.process.group.configuration.CloudProxyProcessGroupConfiguration
import app.simplecloud.simplecloud.api.process.group.configuration.CloudServerProcessGroupConfiguration
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.restserver.annotation.RequestBody
import app.simplecloud.simplecloud.restserver.annotation.RequestMapping
import app.simplecloud.simplecloud.restserver.annotation.RequestPathParam
import app.simplecloud.simplecloud.restserver.annotation.RestController
import app.simplecloud.simplecloud.restserver.base.route.RequestType
import app.simplecloud.simplecloud.restserver.controller.Controller
import app.simplecloud.simplecloud.restserver.defaultcontroller.v1.handler.ProcessGroupUpdateHandler
import com.google.inject.Inject
import kotlinx.coroutines.runBlocking

/**
 * Created by IntelliJ IDEA.
 * Date: 28.06.2021
 * Time: 12:43
 * @author Frederick Baier
 */
@RestController(1, "cloud/group")
class ProcessGroupController @Inject constructor(
    private val groupService: CloudProcessGroupService,
    private val groupUpdateHandler: ProcessGroupUpdateHandler
) : Controller {

    @RequestMapping(RequestType.GET, "", "web.cloud.group.get")
    fun handleGroupGetAll(): List<AbstractCloudProcessGroupConfiguration> {
        val groups = this.groupService.findAll().join()
        return groups.map { it.toConfiguration() }
    }

    @RequestMapping(RequestType.GET, "{name}", "web.cloud.group.get")
    fun handleGroupGetOne(@RequestPathParam("name") name: String): AbstractCloudProcessGroupConfiguration {
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
                CloudLobbyProcessGroupConfiguration::class,
                CloudProxyProcessGroupConfiguration::class,
                CloudServerProcessGroupConfiguration::class
            ]
        ) configuration: AbstractCloudProcessGroupConfiguration
    ): AbstractCloudProcessGroupConfiguration {
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
                CloudLobbyProcessGroupConfiguration::class,
                CloudProxyProcessGroupConfiguration::class,
                CloudServerProcessGroupConfiguration::class
            ]
        ) configuration: AbstractCloudProcessGroupConfiguration
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