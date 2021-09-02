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

package eu.thesimplecloud.simplecloud.api.impl.service

import com.ea.async.Async.await
import com.google.inject.Inject
import com.google.inject.Singleton
import eu.thesimplecloud.simplecloud.api.future.isCompletedNormally
import eu.thesimplecloud.simplecloud.api.impl.request.group.ProcessGroupDeleteRequest
import eu.thesimplecloud.simplecloud.api.impl.request.group.ProcessGroupCreateRequest
import eu.thesimplecloud.simplecloud.api.impl.request.group.update.CloudLobbyGroupUpdateRequest
import eu.thesimplecloud.simplecloud.api.impl.request.group.update.CloudProxyGroupUpdateRequest
import eu.thesimplecloud.simplecloud.api.impl.request.group.update.CloudServerGroupUpdateRequest
import eu.thesimplecloud.simplecloud.api.impl.repository.ignite.IgniteCloudProcessGroupRepository
import eu.thesimplecloud.simplecloud.api.impl.process.group.factory.CloudProcessGroupFactory
import eu.thesimplecloud.simplecloud.api.internal.service.IInternalCloudProcessGroupService
import eu.thesimplecloud.simplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import eu.thesimplecloud.simplecloud.api.validator.GroupConfigurationValidator
import eu.thesimplecloud.simplecloud.api.process.group.ICloudLobbyGroup
import eu.thesimplecloud.simplecloud.api.process.group.ICloudProxyGroup
import eu.thesimplecloud.simplecloud.api.process.group.ICloudServerGroup
import eu.thesimplecloud.simplecloud.api.request.group.IProcessGroupDeleteRequest
import eu.thesimplecloud.simplecloud.api.request.group.IProcessGroupCreateRequest
import eu.thesimplecloud.simplecloud.api.request.group.update.ICloudProcessGroupUpdateRequest
import eu.thesimplecloud.simplecloud.api.utils.future.CloudCompletableFuture
import java.lang.IllegalArgumentException
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 14.06.2021
 * Time: 13:23
 * @author Frederick Baier
 */
open class DefaultCloudProcessGroupService(
    private val groupConfigurationValidator: GroupConfigurationValidator,
    private val igniteRepository: IgniteCloudProcessGroupRepository,
    private val processGroupFactory: CloudProcessGroupFactory
) : IInternalCloudProcessGroupService {

    override fun findByName(name: String): CompletableFuture<ICloudProcessGroup> {
        val completableFuture = this.igniteRepository.find(name)
        return completableFuture.thenApply { this.processGroupFactory.create(it) }
    }

    override fun findAll(): CompletableFuture<List<ICloudProcessGroup>> {
        val completableFuture = this.igniteRepository.findAll()
        return completableFuture.thenApply { list -> list.map { this.processGroupFactory.create(it) } }
    }

    override fun createGroupCreateRequest(configuration: AbstractCloudProcessGroupConfiguration): IProcessGroupCreateRequest {
        return ProcessGroupCreateRequest(this, configuration)
    }

    override fun createGroupDeleteRequest(group: ICloudProcessGroup): IProcessGroupDeleteRequest {
        return ProcessGroupDeleteRequest(this, group)
    }

    override fun createGroupUpdateRequest(group: ICloudProcessGroup): ICloudProcessGroupUpdateRequest {
        return when (group) {
            is ICloudLobbyGroup -> CloudLobbyGroupUpdateRequest(this, group)
            is ICloudProxyGroup -> CloudProxyGroupUpdateRequest(this, group)
            is ICloudServerGroup -> CloudServerGroupUpdateRequest(this, group)
            else -> throw IllegalArgumentException("Unknown group type: ${group::class.java.name}")
        }
    }

    override fun updateGroupInternal(configuration: AbstractCloudProcessGroupConfiguration): CompletableFuture<ICloudProcessGroup> {
        await(this.groupConfigurationValidator.validate(configuration))
        val group = this.processGroupFactory.create(configuration)
        return updateGroupInternal0(group)
    }

    protected open fun updateGroupInternal0(group: ICloudProcessGroup): CompletableFuture<ICloudProcessGroup> {
        this.igniteRepository.save(group.getName(), group.toConfiguration())
        return CloudCompletableFuture.completedFuture(group)
    }

    override fun deleteGroupInternal(group: ICloudProcessGroup) {
        this.igniteRepository.remove(group.getName())
    }

    override fun createGroupInternal(configuration: AbstractCloudProcessGroupConfiguration): CompletableFuture<ICloudProcessGroup> {
        await(this.groupConfigurationValidator.validate(configuration))
        val group = this.processGroupFactory.create(configuration)
        return updateGroupInternal0(group)
    }
}