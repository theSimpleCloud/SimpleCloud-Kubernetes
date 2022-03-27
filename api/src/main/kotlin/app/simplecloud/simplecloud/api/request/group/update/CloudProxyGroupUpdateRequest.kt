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
import app.simplecloud.simplecloud.api.process.group.CloudProxyGroup
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 06.04.2021
 * Time: 11:19
 * @author Frederick Baier
 */
interface CloudProxyGroupUpdateRequest : CloudProcessGroupUpdateRequest {

    /**
     * Sets the start priority for the group
     * @return this
     */
    fun setStartPort(startPort: Int): CloudProxyGroupUpdateRequest

    override fun getProcessGroup(): CloudProxyGroup

    override fun setMaxMemory(memory: Int): CloudProxyGroupUpdateRequest

    override fun setMaxPlayers(players: Int): CloudProxyGroupUpdateRequest

    override fun setImage(image: Image?): CloudProxyGroupUpdateRequest

    override fun setMaintenance(maintenance: Boolean): CloudProxyGroupUpdateRequest

    override fun setJoinPermission(permission: String?): CloudProxyGroupUpdateRequest

    override fun setStateUpdating(stateUpdating: Boolean): CloudProxyGroupUpdateRequest

    override fun setStartPriority(priority: Int): CloudProxyGroupUpdateRequest

    override fun submit(): CompletableFuture<Unit>

}