/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package app.simplecloud.simplecloud.node.service

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.impl.process.group.factory.CloudProcessGroupFactory
import app.simplecloud.simplecloud.api.impl.repository.ignite.IgniteCloudProcessGroupRepository
import app.simplecloud.simplecloud.api.impl.service.AbstractCloudProcessGroupService
import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.validator.GroupConfigurationValidator
import app.simplecloud.simplecloud.node.onlinestrategy.NodeProcessOnlineStrategyService
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