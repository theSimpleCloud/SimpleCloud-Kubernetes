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

import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.service.PermissionGroupService
import app.simplecloud.simplecloud.restserver.annotation.RequestBody
import app.simplecloud.simplecloud.restserver.annotation.RequestMapping
import app.simplecloud.simplecloud.restserver.annotation.RequestPathParam
import app.simplecloud.simplecloud.restserver.annotation.RestController
import app.simplecloud.simplecloud.restserver.base.route.RequestType
import app.simplecloud.simplecloud.restserver.controller.Controller
import com.google.inject.Inject

/**
 * Created by IntelliJ IDEA.
 * Date: 28.06.2021
 * Time: 12:43
 * @author Frederick Baier
 */
@RestController(1, "cloud/permissiongroup")
class PermissionGroupController @Inject constructor(
    private val groupService: PermissionGroupService,
    private val permissionFactory: Permission.Factory
) : Controller {

    @RequestMapping(RequestType.GET, "", "web.cloud.permissiongroup.get")
    fun handleGroupGetAll(): List<PermissionGroupConfiguration> {
        val groups = this.groupService.findAll().join()
        return groups.map { it.toConfiguration() }
    }

    @RequestMapping(RequestType.GET, "{name}", "web.cloud.permissiongroup.get")
    fun handleGroupGetOne(@RequestPathParam("name") name: String): PermissionGroupConfiguration {
        val group = this.groupService.findByName(name).join()
        return group.toConfiguration()
    }

    @RequestMapping(RequestType.POST, "", "web.cloud.permissiongroup.create")
    fun handleGroupCreate(
        @RequestBody(
            types = [],
            classes = [PermissionGroupConfiguration::class]
        ) configuration: PermissionGroupConfiguration
    ): PermissionGroupConfiguration {
        val completableFuture = this.groupService.createCreateRequest(configuration).submit()
        val group = completableFuture.join()
        return group.toConfiguration()
    }

    @RequestMapping(RequestType.PUT, "", "web.cloud.permissiongroup.update")
    fun handleGroupUpdate(
        @RequestBody(
            types = [],
            classes = [PermissionGroupConfiguration::class]
        ) configuration: PermissionGroupConfiguration
    ): Boolean {
        val permissionGroup = this.groupService.findByName(configuration.name).join()
        val updateRequest = this.groupService.createUpdateRequest(permissionGroup)
        val permissions = configuration.permissions.map { this.permissionFactory.create(it) }
        updateRequest.clearPermissions()
        permissions.forEach { updateRequest.addPermission(it) }
        updateRequest.setPriority(configuration.priority)
        updateRequest.submit().join()
        return true
    }

    @RequestMapping(RequestType.DELETE, "{name}", "we.cloud.permissiongroup.delete")
    fun handleDelete(@RequestPathParam("name") groupName: String): Boolean {
        val group = this.groupService.findByName(groupName).join()
        val groupDeleteRequest = this.groupService.createDeleteRequest(group)
        groupDeleteRequest.submit().join()
        return true
    }


}