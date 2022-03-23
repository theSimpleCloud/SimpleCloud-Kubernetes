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

package app.simplecloud.simplecloud.restserver.service

import app.simplecloud.simplecloud.api.future.CloudCompletableFuture
import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.future.nonNull
import app.simplecloud.simplecloud.api.impl.process.group.factory.CloudProcessGroupFactory
import app.simplecloud.simplecloud.api.impl.request.group.ProcessGroupCreateRequestImpl
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessGroupService
import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import app.simplecloud.simplecloud.api.request.group.ProcessGroupCreateRequest
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

    override fun createGroupCreateRequest(configuration: AbstractCloudProcessGroupConfiguration): ProcessGroupCreateRequest {
        return ProcessGroupCreateRequestImpl(this, configuration)
    }
}