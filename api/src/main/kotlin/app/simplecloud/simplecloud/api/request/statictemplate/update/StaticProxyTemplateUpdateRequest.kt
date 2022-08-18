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

package app.simplecloud.simplecloud.api.request.statictemplate.update

import app.simplecloud.simplecloud.api.image.Image
import app.simplecloud.simplecloud.api.request.template.ProcessProxyTemplateUpdateRequest
import app.simplecloud.simplecloud.api.template.static.StaticProxyTemplate

/**
 * Date: 17.08.22
 * Time: 13:10
 * @author Frederick Baier
 *
 */
interface StaticProxyTemplateUpdateRequest : StaticProcessTemplateUpdateRequest, ProcessProxyTemplateUpdateRequest {

    override fun getProcessTemplate(): StaticProxyTemplate

    override fun setStartPort(startPort: Int): StaticProxyTemplateUpdateRequest

    override fun setMaxMemory(memory: Int): StaticProxyTemplateUpdateRequest

    override fun setMaxPlayers(players: Int): StaticProxyTemplateUpdateRequest

    override fun setImage(image: Image?): StaticProxyTemplateUpdateRequest

    override fun setMaintenance(maintenance: Boolean): StaticProxyTemplateUpdateRequest

    override fun setJoinPermission(permission: String?): StaticProxyTemplateUpdateRequest

    override fun setStateUpdating(stateUpdating: Boolean): StaticProxyTemplateUpdateRequest

    override fun setStartPriority(priority: Int): StaticProxyTemplateUpdateRequest

}