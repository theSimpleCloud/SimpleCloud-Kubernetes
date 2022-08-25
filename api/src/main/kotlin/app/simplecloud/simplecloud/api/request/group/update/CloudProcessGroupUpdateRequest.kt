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

package app.simplecloud.simplecloud.api.request.group.update

import app.simplecloud.simplecloud.api.image.Image
import app.simplecloud.simplecloud.api.request.template.ProcessTemplateUpdateRequest
import app.simplecloud.simplecloud.api.template.group.CloudProcessGroup

/**
 * Created by IntelliJ IDEA.
 * Date: 17.03.2021
 * Time: 18:59
 * @author Frederick Baier
 *
 * Request for updating a registered group
 *
 */
interface CloudProcessGroupUpdateRequest : ProcessTemplateUpdateRequest {

    override fun getProcessTemplate(): CloudProcessGroup

    override fun setMaxMemory(memory: Int): CloudProcessGroupUpdateRequest

    override fun setMaxPlayers(players: Int): CloudProcessGroupUpdateRequest

    override fun setImage(image: Image?): CloudProcessGroupUpdateRequest

    override fun setMaintenance(maintenance: Boolean): CloudProcessGroupUpdateRequest

    override fun setJoinPermission(permission: String?): CloudProcessGroupUpdateRequest

    override fun setStateUpdating(stateUpdating: Boolean): CloudProcessGroupUpdateRequest

    override fun setStartPriority(priority: Int): CloudProcessGroupUpdateRequest

    override fun setActive(active: Boolean): CloudProcessGroupUpdateRequest

}