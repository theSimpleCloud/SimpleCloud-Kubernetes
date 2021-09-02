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

package eu.thesimplecloud.simplecloud.node.service

import com.google.inject.Inject
import eu.thesimplecloud.simplecloud.api.impl.process.group.factory.CloudProcessGroupFactory
import eu.thesimplecloud.simplecloud.api.impl.repository.ignite.IgniteCloudProcessGroupRepository
import eu.thesimplecloud.simplecloud.api.impl.service.DefaultCloudProcessGroupService
import eu.thesimplecloud.simplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.simplecloud.api.validator.GroupConfigurationValidator
import eu.thesimplecloud.simplecloud.node.mongo.group.CombinedProcessGroupEntity
import eu.thesimplecloud.simplecloud.node.mongo.group.MongoCloudProcessGroupRepository
import java.util.concurrent.CompletableFuture

class CloudProcessGroupServiceImpl @Inject constructor(
    groupConfigurationValidator: GroupConfigurationValidator,
    igniteRepository: IgniteCloudProcessGroupRepository,
    processGroupFactory: CloudProcessGroupFactory,
    private val mongoCloudProcessGroupRepository: MongoCloudProcessGroupRepository
) : DefaultCloudProcessGroupService(
    groupConfigurationValidator, igniteRepository, processGroupFactory
) {

    override fun updateGroupInternal0(group: ICloudProcessGroup): CompletableFuture<ICloudProcessGroup> {
        val result =  super.updateGroupInternal0(group)
        saveToDatabase(group)
        return result
    }

    override fun deleteGroupInternal(group: ICloudProcessGroup) {
        super.deleteGroupInternal(group)
        deleteGroupInDatabase(group)
    }

    private fun deleteGroupInDatabase(group: ICloudProcessGroup) {
        this.mongoCloudProcessGroupRepository.remove(group.getName())
    }

    private fun saveToDatabase(group: ICloudProcessGroup) {
        val combinedProcessGroupEntity = CombinedProcessGroupEntity.fromGroupConfiguration(group.toConfiguration())
        this.mongoCloudProcessGroupRepository.save(group.getName(), combinedProcessGroupEntity)
    }


}