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

package app.simplecloud.simplecloud.api.impl.request.template.statictemplates

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.api.internal.service.InternalStaticProcessTemplateService
import app.simplecloud.simplecloud.api.request.statictemplate.StaticProcessTemplateCreateRequest
import app.simplecloud.simplecloud.api.template.configuration.AbstractProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.static.StaticProcessTemplate
import app.simplecloud.simplecloud.api.utils.DefaultNameRequirement
import java.util.concurrent.CompletableFuture

/**
 * Date: 18.08.22
 * Time: 10:31
 * @author Frederick Baier
 *
 */
class StaticProcessCreateRequestImpl(
    private val internalService: InternalStaticProcessTemplateService,
    private val configuration: AbstractProcessTemplateConfiguration,
) : StaticProcessTemplateCreateRequest {

    override fun submit(): CompletableFuture<StaticProcessTemplate> = CloudScope.future {
        DefaultNameRequirement.checkName(configuration.name)
        checkStaticTemplateName()
        checkMaxMemory()
        checkMaxPlayers()
        return@future internalService.createStaticTemplateInternal(configuration)
    }

    private fun checkMaxMemory() {
        if (this.configuration.maxMemory < 256) {
            throw IllegalArgumentException("Memory cannot be lower than 256")
        }
    }

    private fun checkMaxPlayers() {
        if (this.configuration.maxPlayers < -1) {
            throw IllegalArgumentException("Max Players cannot be lower than -1")
        }
    }

    private suspend fun checkStaticTemplateName() {
        if (doesStaticTemplateExist(configuration.name)) {
            throw IllegalArgumentException("Static Template already exists")
        }
    }

    private suspend fun doesStaticTemplateExist(groupName: String): Boolean {
        return try {
            this.internalService.findByName(groupName).await()
            true
        } catch (e: NoSuchElementException) {
            false
        }
    }
}