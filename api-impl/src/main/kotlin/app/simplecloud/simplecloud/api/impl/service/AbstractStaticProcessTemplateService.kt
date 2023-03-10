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

import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedStaticProcessTemplateRepository
import app.simplecloud.simplecloud.api.impl.request.template.statictemplates.StaticProcessCreateRequestImpl
import app.simplecloud.simplecloud.api.impl.request.template.statictemplates.StaticProcessTemplateDeleteRequestImpl
import app.simplecloud.simplecloud.api.impl.template.statictemplate.factory.UniversalStaticProcessTemplateFactory
import app.simplecloud.simplecloud.api.internal.service.InternalStaticProcessTemplateService
import app.simplecloud.simplecloud.api.request.statictemplate.StaticProcessTemplateCreateRequest
import app.simplecloud.simplecloud.api.request.statictemplate.StaticProcessTemplateDeleteRequest
import app.simplecloud.simplecloud.api.request.statictemplate.update.StaticProcessTemplateUpdateRequest
import app.simplecloud.simplecloud.api.template.configuration.AbstractProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.static.StaticProcessTemplate
import java.util.concurrent.CompletableFuture

/**
 * Date: 17.08.22
 * Time: 10:31
 * @author Frederick Baier
 *
 */
abstract class AbstractStaticProcessTemplateService(
    private val distributedRepository: DistributedStaticProcessTemplateRepository,
    private val staticTemplateFactory: UniversalStaticProcessTemplateFactory,
) : InternalStaticProcessTemplateService {

    override suspend fun createStaticTemplateInternal(configuration: AbstractProcessTemplateConfiguration): StaticProcessTemplate {
        val staticTemplate = this.staticTemplateFactory.create(configuration, this)
        createGroupInternal0(configuration)
        return staticTemplate
    }

    abstract suspend fun createGroupInternal0(configuration: AbstractProcessTemplateConfiguration)

    override suspend fun updateStaticTemplateInternal(configuration: AbstractProcessTemplateConfiguration) {
        updateGroupInternal0(configuration)
    }

    abstract suspend fun updateGroupInternal0(configuration: AbstractProcessTemplateConfiguration)

    override fun findByName(name: String): CompletableFuture<StaticProcessTemplate> {
        val completableFuture = this.distributedRepository.find(name)
        return completableFuture.thenApply { this.staticTemplateFactory.create(it, this) }
    }

    override fun findAll(): CompletableFuture<List<StaticProcessTemplate>> {
        val completableFuture = this.distributedRepository.findAll()
        return completableFuture.thenApply { list -> list.map { this.staticTemplateFactory.create(it, this) } }
    }

    override fun createCreateRequest(configuration: AbstractProcessTemplateConfiguration): StaticProcessTemplateCreateRequest {
        return StaticProcessCreateRequestImpl(this, configuration)
    }

    override fun createUpdateRequest(template: StaticProcessTemplate): StaticProcessTemplateUpdateRequest {
        return template.createUpdateRequest()
    }

    override fun createDeleteRequest(template: StaticProcessTemplate): StaticProcessTemplateDeleteRequest {
        return StaticProcessTemplateDeleteRequestImpl(this, template)
    }
}