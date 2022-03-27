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

package app.simplecloud.simplecloud.api.internal.request.process

import app.simplecloud.simplecloud.api.process.state.ProcessState
import app.simplecloud.simplecloud.api.request.process.ProcessUpdateRequest
import java.util.*

/**
 * Date: 05.01.22
 * Time: 17:13
 * @author Frederick Baier
 *
 * Internal interface, that adds internal methods for updating a process.
 * Do not use this on you own.
 *
 */
interface InternalProcessUpdateRequest : ProcessUpdateRequest {

    /**
     * Sets the ignite id of the process to update
     */
    fun setIgniteId(id: UUID): InternalProcessUpdateRequest

    /**
     * Sets the state of the process to update
     */
    fun setState(processState: ProcessState): InternalProcessUpdateRequest

    /**
     * Sets the amount of online players for the process
     */
    fun setOnlinePlayers(onlinePlayers: Int): InternalProcessUpdateRequest

    override fun setMaxPlayers(maxPlayers: Int): InternalProcessUpdateRequest

    override fun setVisible(visible: Boolean): InternalProcessUpdateRequest

}