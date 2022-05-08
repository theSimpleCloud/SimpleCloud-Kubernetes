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

package app.simplecloud.simplecloud.api.impl.service

import app.simplecloud.simplecloud.api.impl.request.permission.PermissionGroupCreateRequestImpl
import app.simplecloud.simplecloud.api.impl.request.permission.PermissionGroupDeleteRequestImpl
import app.simplecloud.simplecloud.api.impl.request.permission.PermissionGroupUpdateRequestImpl
import app.simplecloud.simplecloud.api.internal.service.InternalPermissionGroupService
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.repository.PermissionGroupRepository
import app.simplecloud.simplecloud.api.request.permission.PermissionGroupCreateRequest
import app.simplecloud.simplecloud.api.request.permission.PermissionGroupDeleteRequest
import app.simplecloud.simplecloud.api.request.permission.PermissionGroupUpdateRequest
import java.util.concurrent.CompletableFuture

/**
 * Date: 20.03.22
 * Time: 18:56
 * @author Frederick Baier
 *
 */
abstract class AbstractPermissionGroupService(
    private val repository: PermissionGroupRepository,
    private val groupFactory: PermissionGroup.Factory,
    private val permissionFactory: Permission.Factory
) : InternalPermissionGroupService {

    override fun findByName(name: String): CompletableFuture<PermissionGroup> {
        val completableFuture = this.repository.find(name)
        return completableFuture.thenApply { this.groupFactory.create(it, this) }
    }

    override fun findAll(): CompletableFuture<List<PermissionGroup>> {
        val completableFuture = this.repository.findAll()
        return completableFuture.thenApply { list -> list.map { this.groupFactory.create(it, this) } }
    }

    override fun createCreateRequest(configuration: PermissionGroupConfiguration): PermissionGroupCreateRequest {
        return PermissionGroupCreateRequestImpl(configuration, this)
    }

    override fun createDeleteRequest(group: PermissionGroup): PermissionGroupDeleteRequest {
        return PermissionGroupDeleteRequestImpl(group, this)
    }

    override fun createUpdateRequest(group: PermissionGroup): PermissionGroupUpdateRequest {
        return PermissionGroupUpdateRequestImpl(group, this, this.permissionFactory)
    }

    override suspend fun createGroupInternal(configuration: PermissionGroupConfiguration): PermissionGroup {
        val permissionGroup = this.groupFactory.create(configuration, this)
        updateGroupInternal(configuration)
        return permissionGroup
    }


}