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

package app.simplecloud.simplecloud.node.service

import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedPermissionGroupRepository
import app.simplecloud.simplecloud.api.impl.service.AbstractPermissionGroupService
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.ResourceRequestHandler
import app.simplecloud.simplecloud.node.resource.permissiongroup.V1Beta1PermissionConfiguration
import app.simplecloud.simplecloud.node.resource.permissiongroup.V1Beta1PermissionGroupSpec

/**
 * Date: 20.03.22
 * Time: 13:39
 * @author Frederick Baier
 *
 */
class PermissionGroupServiceImpl(
    private val distributedRepository: DistributedPermissionGroupRepository,
    private val groupFactory: PermissionGroup.Factory,
    private val permissionFactory: Permission.Factory,
    private val requestHandler: ResourceRequestHandler,
) : AbstractPermissionGroupService(distributedRepository, groupFactory, permissionFactory) {

    override suspend fun createGroupInternal0(configuration: PermissionGroupConfiguration) {
        this.requestHandler.handleCreate(
            "core",
            "PermissionGroup",
            "v1beta1",
            configuration.name,
            convertConfigurationToSpec(configuration)
        )
    }

    override suspend fun updateGroupInternal(configuration: PermissionGroupConfiguration) {
        this.requestHandler.handleUpdate(
            "core",
            "PermissionGroup",
            "v1beta1",
            configuration.name,
            convertConfigurationToSpec(configuration)
        )
    }

    override suspend fun deleteGroupInternal(group: PermissionGroup) {
        this.requestHandler.handleDelete("core", "PermissionGroup", "v1beta1", group.getName())
    }

    private fun convertConfigurationToSpec(configuration: PermissionGroupConfiguration): V1Beta1PermissionGroupSpec {
        return V1Beta1PermissionGroupSpec(
            configuration.priority,
            configuration.permissions.map {
                V1Beta1PermissionConfiguration(
                    it.permissionString,
                    it.active,
                    it.expiresAtTimestamp,
                    it.targetProcessGroup
                )
            }.toTypedArray()
        )
    }

}