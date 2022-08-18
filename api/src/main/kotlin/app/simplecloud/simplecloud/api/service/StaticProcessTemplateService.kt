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

import app.simplecloud.simplecloud.api.request.statictemplate.StaticProcessTemplateCreateRequest
import app.simplecloud.simplecloud.api.request.statictemplate.StaticProcessTemplateDeleteRequest
import app.simplecloud.simplecloud.api.request.statictemplate.update.StaticProcessTemplateUpdateRequest
import app.simplecloud.simplecloud.api.template.configuration.AbstractProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.static.StaticProcessTemplate
import java.util.concurrent.CompletableFuture

/**
 * Date: 16.08.22
 * Time: 21:08
 * @author Frederick Baier
 *
 */
interface StaticProcessTemplateService : Service {

    fun findByName(name: String): CompletableFuture<StaticProcessTemplate>

    fun findAll(): CompletableFuture<List<StaticProcessTemplate>>

    fun createCreateRequest(configuration: AbstractProcessTemplateConfiguration): StaticProcessTemplateCreateRequest

    fun createUpdateRequest(template: StaticProcessTemplate): StaticProcessTemplateUpdateRequest

    fun createDeleteRequest(template: StaticProcessTemplate): StaticProcessTemplateDeleteRequest

}