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

package app.simplecloud.simplecloud.api.impl.permission

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.impl.service.AbstractPermissionGroupService
import app.simplecloud.simplecloud.api.internal.service.InternalPermissionGroupService
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.repository.PermissionGroupRepository

/**
 * Date: 23.03.22
 * Time: 09:01
 * @author Frederick Baier
 *
 */
class PermissionGroupServiceImpl(
    private val repository: PermissionGroupRepository,
    private val groupFactory: PermissionGroup.Factory,
    private val permissionFactory: Permission.Factory
) : AbstractPermissionGroupService(repository, groupFactory, permissionFactory), InternalPermissionGroupService {

    override suspend fun createGroupInternal0(configuration: PermissionGroupConfiguration) {
        this.repository.save(configuration.name, configuration).await()
    }

    override suspend fun updateGroupInternal(configuration: PermissionGroupConfiguration) {
        this.repository.save(configuration.name, configuration).await()
    }

    override suspend fun deleteGroupInternal(group: PermissionGroup) {
        this.repository.remove(group.getName()).await()
    }
}