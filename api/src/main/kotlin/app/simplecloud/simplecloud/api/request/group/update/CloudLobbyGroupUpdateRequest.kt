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
import app.simplecloud.simplecloud.api.process.group.CloudLobbyGroup
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 06.04.2021
 * Time: 09:59
 * @author Frederick Baier
 */
interface CloudLobbyGroupUpdateRequest : CloudServerGroupUpdateRequest {

    /**
     * Sets the lobby priority for the group
     * @return this
     */
    fun setLobbyPriority(lobbyPriority: Int): CloudLobbyGroupUpdateRequest

    override fun getProcessGroup(): CloudLobbyGroup

    override fun setMaxMemory(memory: Int): CloudLobbyGroupUpdateRequest

    override fun setMaxPlayers(players: Int): CloudLobbyGroupUpdateRequest

    override fun setImage(image: Image?): CloudLobbyGroupUpdateRequest

    override fun setMaintenance(maintenance: Boolean): CloudLobbyGroupUpdateRequest

    override fun setJoinPermission(permission: String?): CloudLobbyGroupUpdateRequest

    override fun setStateUpdating(stateUpdating: Boolean): CloudLobbyGroupUpdateRequest

    override fun setStartPriority(priority: Int): CloudLobbyGroupUpdateRequest

    override fun submit(): CompletableFuture<Unit>
}