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

import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedCloudProcessGroupRepository
import app.simplecloud.simplecloud.api.impl.request.template.group.CloudProcessGroupCreateRequestImpl
import app.simplecloud.simplecloud.api.impl.request.template.group.CloudProcessGroupDeleteRequestImpl
import app.simplecloud.simplecloud.api.impl.template.group.factory.UniversalCloudProcessGroupFactory
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessGroupService
import app.simplecloud.simplecloud.api.request.group.CloudProcessGroupCreateRequest
import app.simplecloud.simplecloud.api.request.group.CloudProcessGroupDeleteRequest
import app.simplecloud.simplecloud.api.request.group.update.CloudProcessGroupUpdateRequest
import app.simplecloud.simplecloud.api.template.configuration.AbstractProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.group.CloudProcessGroup
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 14.06.2021
 * Time: 13:23
 * @author Frederick Baier
 */
abstract class AbstractCloudProcessGroupService(
    private val distributedRepository: DistributedCloudProcessGroupRepository,
    private val processGroupFactory: UniversalCloudProcessGroupFactory,
) : InternalCloudProcessGroupService {

    override fun findByName(name: String): CompletableFuture<CloudProcessGroup> {
        val completableFuture = this.distributedRepository.find(name)
        return completableFuture.thenApply { this.processGroupFactory.create(it, this) }
    }

    override fun findAll(): CompletableFuture<List<CloudProcessGroup>> {
        val completableFuture = this.distributedRepository.findAll()
        return completableFuture.thenApply { list -> list.map { this.processGroupFactory.create(it, this) } }
    }

    override fun createCreateRequest(configuration: AbstractProcessTemplateConfiguration): CloudProcessGroupCreateRequest {
        return CloudProcessGroupCreateRequestImpl(this, configuration)
    }

    override fun createUpdateRequest(group: CloudProcessGroup): CloudProcessGroupUpdateRequest {
        //The returned request object depends on the type of the group. So the request is created within the group.
        return group.createUpdateRequest()
    }

    override fun createDeleteRequest(group: CloudProcessGroup): CloudProcessGroupDeleteRequest {
        return CloudProcessGroupDeleteRequestImpl(this, group)
    }

    override suspend fun updateGroupInternal(configuration: AbstractProcessTemplateConfiguration) {
        val group = this.processGroupFactory.create(configuration, this)
        updateGroupInternal0(group)
    }

    abstract suspend fun updateGroupInternal0(group: CloudProcessGroup)

    override suspend fun createGroupInternal(configuration: AbstractProcessTemplateConfiguration): CloudProcessGroup {
        val group = this.processGroupFactory.create(configuration, this)
        updateGroupInternal0(group)
        return group
    }
}