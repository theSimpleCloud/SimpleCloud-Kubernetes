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

package eu.thesimplecloud.simplecloud.restserver.defaultcontroller.v1

import com.ea.async.Async.await
import com.google.inject.Inject
import eu.thesimplecloud.simplecloud.restserver.annotation.*
import eu.thesimplecloud.simplecloud.restserver.controller.Controller
import eu.thesimplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import eu.thesimplecloud.simplecloud.api.process.group.configuration.CloudLobbyProcessGroupConfiguration
import eu.thesimplecloud.simplecloud.api.process.group.configuration.CloudProxyProcessGroupConfiguration
import eu.thesimplecloud.simplecloud.api.process.group.configuration.CloudServerProcessGroupConfiguration
import eu.thesimplecloud.simplecloud.api.service.*
import eu.thesimplecloud.simplecloud.restserver.defaultcontroller.v1.handler.ProcessGroupUpdateHandler

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
        val groups = await(this.groupService.findAll())
        return groups.map { it.toConfiguration() }
    }

    @RequestMapping(RequestType.GET, "{name}", "web.cloud.group.get")
    fun handleGroupGetOne(@RequestPathParam("name") name: String): AbstractCloudProcessGroupConfiguration {
        val group = await(this.groupService.findByName(name))
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
        val completableFuture = this.groupService.createGroupCreateRequest(configuration).submit()
        val group = await(completableFuture)
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
    ): Boolean {
        this.groupUpdateHandler.update(configuration)
        return true
    }

    @RequestMapping(RequestType.DELETE, "{name}", "we.cloud.group.delete")
    fun handleDelete(@RequestPathParam("name") groupName: String): Boolean {
        val group = await(this.groupService.findByName(groupName))
        val groupDeleteRequest = group.createDeleteRequest()
        await(groupDeleteRequest.submit())
        return true
    }



}