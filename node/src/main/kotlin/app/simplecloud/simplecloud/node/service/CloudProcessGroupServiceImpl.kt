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
import app.simplecloud.simplecloud.api.impl.process.group.factory.CloudProcessGroupFactory
import app.simplecloud.simplecloud.api.impl.repository.ignite.IgniteCloudProcessGroupRepository
import app.simplecloud.simplecloud.api.impl.service.AbstractCloudProcessGroupService
import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.service.NodeProcessOnlineStrategyService
import app.simplecloud.simplecloud.api.validator.GroupConfigurationValidator
import app.simplecloud.simplecloud.node.repository.mongo.group.CombinedProcessGroupEntity
import app.simplecloud.simplecloud.node.repository.mongo.group.MongoCloudProcessGroupRepository
import com.google.inject.Inject
import com.google.inject.Singleton

@Singleton
class CloudProcessGroupServiceImpl @Inject constructor(
    groupConfigurationValidator: GroupConfigurationValidator,
    processGroupFactory: CloudProcessGroupFactory,
    private val igniteRepository: IgniteCloudProcessGroupRepository,
    private val mongoCloudProcessGroupRepository: MongoCloudProcessGroupRepository,
    private val nodeProcessOnlineStrategyService: NodeProcessOnlineStrategyService
) : AbstractCloudProcessGroupService(
    groupConfigurationValidator, igniteRepository, processGroupFactory
) {

    override suspend fun updateGroupInternal0(group: CloudProcessGroup) {
        this.igniteRepository.save(group.getName(), group.toConfiguration()).await()
        this.nodeProcessOnlineStrategyService.checkProcessOnlineCount()
        saveToDatabase(group)
    }

    override suspend fun deleteGroupInternal(group: CloudProcessGroup) {
        this.igniteRepository.remove(group.getName())
        deleteGroupFromDatabase(group)
    }

    private fun deleteGroupFromDatabase(group: CloudProcessGroup) {
        this.mongoCloudProcessGroupRepository.remove(group.getName())
    }

    private fun saveToDatabase(group: CloudProcessGroup) {
        val combinedProcessGroupEntity = CombinedProcessGroupEntity.fromGroupConfiguration(group.toConfiguration())
        this.mongoCloudProcessGroupRepository.save(group.getName(), combinedProcessGroupEntity)
    }


}