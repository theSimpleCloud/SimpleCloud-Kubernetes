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

package app.simplecloud.simplecloud.restserver.service

import app.simplecloud.simplecloud.api.future.CloudCompletableFuture
import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.future.nonNull
import app.simplecloud.simplecloud.api.impl.process.group.factory.CloudProcessGroupFactory
import app.simplecloud.simplecloud.api.impl.request.group.CloudProcessGroupCreateRequestImpl
import app.simplecloud.simplecloud.api.impl.request.group.CloudProcessGroupDeleteRequestImpl
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessGroupService
import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import app.simplecloud.simplecloud.api.request.group.CloudProcessGroupCreateRequest
import app.simplecloud.simplecloud.api.request.group.CloudProcessGroupDeleteRequest
import app.simplecloud.simplecloud.api.request.group.update.CloudProcessGroupUpdateRequest
import app.simplecloud.simplecloud.api.validator.ValidatorService
import com.google.inject.Inject
import com.google.inject.Singleton
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by IntelliJ IDEA.
 * Date: 02/07/2021
 * Time: 13:58
 * @author Frederick Baier
 */
@Singleton
class TestCloudProcessGroupService @Inject constructor(
    private val validatorService: ValidatorService,
    private val groupFactory: CloudProcessGroupFactory
) : InternalCloudProcessGroupService {

    private val groups = ConcurrentHashMap<String, CloudProcessGroup>()
    private val validator = validatorService.getValidator(AbstractCloudProcessGroupConfiguration::class.java)

    override suspend fun updateGroupInternal(configuration: AbstractCloudProcessGroupConfiguration) {
        this.validator.validate(configuration).await()
        val group = this.groupFactory.create(configuration)
        updateGroupInternal0(group)
    }

    override suspend fun deleteGroupInternal(group: CloudProcessGroup) {
        this.groups.remove(group.getIdentifier())
    }

    override suspend fun createGroupInternal(configuration: AbstractCloudProcessGroupConfiguration): CloudProcessGroup {
        this.validator.validate(configuration).await()
        val group = this.groupFactory.create(configuration)
        return updateGroupInternal0(group)
    }

    private fun updateGroupInternal0(group: CloudProcessGroup): CloudProcessGroup {
        this.groups[group.getIdentifier()] = group
        return group
    }

    override fun findByName(name: String): CompletableFuture<CloudProcessGroup> {
        return CloudCompletableFuture.supplyAsync {
            this.groups[name] ?: throw NoSuchElementException("Group does not exist")
        }.nonNull()
    }

    override fun findAll(): CompletableFuture<List<CloudProcessGroup>> {
        return CloudCompletableFuture.supplyAsync { this.groups.values.toList() }.nonNull()
    }

    override fun createCreateRequest(configuration: AbstractCloudProcessGroupConfiguration): CloudProcessGroupCreateRequest {
        return CloudProcessGroupCreateRequestImpl(this, configuration)
    }

    override fun createDeleteRequest(group: CloudProcessGroup): CloudProcessGroupDeleteRequest {
        return CloudProcessGroupDeleteRequestImpl(this, group)
    }

    override fun createUpdateRequest(group: CloudProcessGroup): CloudProcessGroupUpdateRequest {
        return group.createUpdateRequest()
    }


}