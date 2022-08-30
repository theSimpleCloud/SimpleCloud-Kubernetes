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

package app.simplecloud.simplecloud.node.util

import app.simplecloud.simplecloud.api.CloudAPI
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.configuration.PermissionConfiguration
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration

/**
 * Date: 29.08.22
 * Time: 19:11
 * @author Frederick Baier
 *
 */
interface TestPermissionGroupProvider {

    fun getCloudAPI(): CloudAPI

    fun givenPermissionGroup(name: String): PermissionGroup {
        val configuration = createPermissionGroupConfiguration(name)
        return getCloudAPI().getPermissionGroupService().createCreateRequest(configuration).submit().join()
    }

    fun createPermissionGroupConfiguration(name: String = "Admin"): PermissionGroupConfiguration {
        return PermissionGroupConfiguration(
            name,
            0,
            emptyList()
        )
    }

    fun createPermissionGroupConfigurationWithPermission(permission: PermissionConfiguration): PermissionGroupConfiguration {
        return PermissionGroupConfiguration(
            "Test",
            0,
            listOf(permission)
        )
    }

}