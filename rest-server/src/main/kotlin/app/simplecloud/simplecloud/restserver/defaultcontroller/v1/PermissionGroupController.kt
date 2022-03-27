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