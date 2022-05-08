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

package app.simplecloud.simplecloud.api.permission

import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.service.PermissionGroupService

/**
 * Date: 19.03.22
 * Time: 12:00
 * @author Frederick Baier
 *
 */
interface PermissionGroup : PermissionEntity {

    /**
     * Returns the name of the group
     */
    fun getName(): String

    /**
     * Returns the priority of this group (higher is better)
     */
    fun getPriority(): Int

    /**
     * Return the configuration of this permission group
     */
    fun toConfiguration(): PermissionGroupConfiguration

    interface Factory {

        fun create(configuration: PermissionGroupConfiguration, groupService: PermissionGroupService): PermissionGroup

    }

}