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

package app.simplecloud.simplecloud.api.impl.request.template.statictemplates.update

import app.simplecloud.simplecloud.api.image.Image
import app.simplecloud.simplecloud.api.impl.request.template.update.AbstractProxyTemplateUpdateRequest
import app.simplecloud.simplecloud.api.internal.service.InternalStaticProcessTemplateService
import app.simplecloud.simplecloud.api.request.statictemplate.update.StaticProxyTemplateUpdateRequest
import app.simplecloud.simplecloud.api.template.configuration.ProxyProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.static.StaticProxyTemplate

/**
 * Date: 17.08.22
 * Time: 14:58
 * @author Frederick Baier
 *
 */
class StaticProxyTemplateUpdateRequestImpl(
    private val staticTemplate: StaticProxyTemplate,
    private val internalService: InternalStaticProcessTemplateService,
) : AbstractProxyTemplateUpdateRequest(staticTemplate), StaticProxyTemplateUpdateRequest {

    override fun getProcessTemplate(): StaticProxyTemplate {
        return this.staticTemplate
    }

    override fun setStartPort(startPort: Int): StaticProxyTemplateUpdateRequest {
        super.setStartPort(startPort)
        return this
    }

    override fun setMaxMemory(memory: Int): StaticProxyTemplateUpdateRequest {
        super.setMaxMemory(memory)
        return this
    }

    override fun setMaxPlayers(players: Int): StaticProxyTemplateUpdateRequest {
        super.setMaxPlayers(players)
        return this
    }

    override fun setImage(image: Image?): StaticProxyTemplateUpdateRequest {
        super.setImage(image)
        return this
    }

    override fun setMaintenance(maintenance: Boolean): StaticProxyTemplateUpdateRequest {
        super.setMaintenance(maintenance)
        return this
    }

    override fun setJoinPermission(permission: String?): StaticProxyTemplateUpdateRequest {
        super.setJoinPermission(permission)
        return this
    }

    override fun setStateUpdating(stateUpdating: Boolean): StaticProxyTemplateUpdateRequest {
        super.setStateUpdating(stateUpdating)
        return this
    }

    override fun setStartPriority(priority: Int): StaticProxyTemplateUpdateRequest {
        super.setStartPriority(priority)
        return this
    }

    override suspend fun submit0(image: Image?) {
        val updateObj = ProxyProcessTemplateConfiguration(
            this.staticTemplate.getName(),
            this.maxMemory,
            this.maxPlayers,
            this.maintenance,
            image?.getName(),
            this.stateUpdating,
            this.startPriority,
            this.joinPermission,
            this.startPort
        )
        this.internalService.updateStaticTemplateInternal(updateObj)
    }

}