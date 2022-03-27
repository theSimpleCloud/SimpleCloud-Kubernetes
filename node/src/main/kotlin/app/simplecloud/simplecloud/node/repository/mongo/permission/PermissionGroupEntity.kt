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

package app.simplecloud.simplecloud.node.repository.mongo.permission

import app.simplecloud.simplecloud.api.permission.configuration.PermissionConfiguration
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import dev.morphia.annotations.Entity
import dev.morphia.annotations.Id

/**
 * Date: 20.03.22
 * Time: 13:21
 * @author Frederick Baier
 *
 */
@Entity("permission_groups")
class PermissionGroupEntity(
    @Id
    val name: String,
    val priority: Int,
    val permissions: List<PermissionConfiguration>
) {

    private constructor() : this(
        "<empty>",
        -1,
        emptyList()
    )

    fun toConfiguration(): PermissionGroupConfiguration {
        return PermissionGroupConfiguration(
            this.name,
            this.priority,
            this.permissions
        )
    }

    companion object {
        fun fromConfiguration(configuration: PermissionGroupConfiguration): PermissionGroupEntity {
            return PermissionGroupEntity(configuration.name, configuration.priority, configuration.permissions)
        }
    }

}