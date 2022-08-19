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

package app.simplecloud.simplecloud.api.service

import app.simplecloud.simplecloud.api.request.template.ProcessTemplateCreateRequest
import app.simplecloud.simplecloud.api.request.template.ProcessTemplateDeleteRequest
import app.simplecloud.simplecloud.api.request.template.ProcessTemplateUpdateRequest
import app.simplecloud.simplecloud.api.template.ProcessTemplate
import app.simplecloud.simplecloud.api.template.configuration.AbstractProcessTemplateConfiguration
import java.util.concurrent.CompletableFuture

/**
 * Date: 19.08.22
 * Time: 09:00
 * @author Frederick Baier
 *
 */
interface ProcessTemplateService<T : ProcessTemplate> : Service {

    /**
     * Returns the process template found by [name] or the futures fails with [NoSuchElementException]
     */
    fun findByName(name: String): CompletableFuture<T>

    /**
     * Returns all process templates
     */
    fun findAll(): CompletableFuture<List<T>>

    /**
     * Creates a request to create a new process template
     */
    fun createCreateRequest(configuration: AbstractProcessTemplateConfiguration): ProcessTemplateCreateRequest<T>

    /**
     * Creates a request to update an existing process template
     * The returned request type depends on the type of the [template]
     * @see [ProcessTemplate.createUpdateRequest]
     */
    fun createUpdateRequest(template: T): ProcessTemplateUpdateRequest

    /**
     * Creates a request to delete an existing process template
     */
    fun createDeleteRequest(template: T): ProcessTemplateDeleteRequest

}