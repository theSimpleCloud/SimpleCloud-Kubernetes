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

package app.simplecloud.simplecloud.node.api.permission

import app.simplecloud.simplecloud.api.CloudAPI
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.configuration.PermissionConfiguration
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.service.PermissionGroupService

/**
 * Date: 20.08.22
 * Time: 09:39
 * @author Frederick Baier
 *
 */
abstract class PermissionGroupServiceBaseTest {

    protected lateinit var groupService: PermissionGroupService

    protected lateinit var permissionFactory: Permission.Factory

    open fun setUp() {
        val cloudAPI = getCloudAPI()
        this.groupService = cloudAPI.getPermissionGroupService()
        this.permissionFactory = cloudAPI.getPermissionFactory()
    }

    abstract fun getCloudAPI(): CloudAPI

    protected fun createPermissionGroupConfiguration(name: String = "Admin"): PermissionGroupConfiguration {
        return PermissionGroupConfiguration(
            name,
            0,
            emptyList()
        )
    }

    protected fun createPermissionGroupConfigurationWithPermission(permission: PermissionConfiguration): PermissionGroupConfiguration {
        return PermissionGroupConfiguration(
            "Test",
            0,
            listOf(permission)
        )
    }

}