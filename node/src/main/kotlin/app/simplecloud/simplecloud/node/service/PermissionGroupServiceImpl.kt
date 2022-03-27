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

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.impl.repository.ignite.IgnitePermissionGroupRepository
import app.simplecloud.simplecloud.api.impl.service.AbstractPermissionGroupService
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.node.repository.mongo.permission.MongoPermissionGroupRepository
import app.simplecloud.simplecloud.node.repository.mongo.permission.PermissionGroupEntity
import com.google.inject.Inject
import com.google.inject.Singleton

/**
 * Date: 20.03.22
 * Time: 13:39
 * @author Frederick Baier
 *
 */
@Singleton
class PermissionGroupServiceImpl @Inject constructor(
    private val mongoRepository: MongoPermissionGroupRepository,
    private val igniteRepository: IgnitePermissionGroupRepository,
    private val groupFactory: PermissionGroup.Factory,
    private val permissionFactory: Permission.Factory,
) : AbstractPermissionGroupService(igniteRepository, groupFactory, permissionFactory) {

    override suspend fun updateGroupInternal(configuration: PermissionGroupConfiguration) {
        this.igniteRepository.save(configuration.name, configuration).await()
        saveToDatabase(configuration)
    }

    private fun saveToDatabase(configuration: PermissionGroupConfiguration) {
        val groupEntity = PermissionGroupEntity.fromConfiguration(configuration)
        this.mongoRepository.save(groupEntity.name, groupEntity)
    }

    override suspend fun deleteGroupInternal(group: PermissionGroup) {
        this.igniteRepository.remove(group.getName())
        deleteGroupFromDatabase(group)
    }

    private fun deleteGroupFromDatabase(group: PermissionGroup) {
        this.mongoRepository.remove(group.getName())
    }

}