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

package app.simplecloud.simplecloud.api.request.process

import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.utils.Request

/**
 * Created by IntelliJ IDEA.
 * Date: 16.03.2021
 * Time: 20:09
 * @author Frederick Baier
 *
 * Used to update a process
 *
 */
interface ProcessUpdateRequest : Request<Unit> {

    /**
     * Returns the process this request will update
     */
    fun getProcess(): CloudProcess

    /**
     * Sets the max players for the new process
     * @return this
     */
    fun setMaxPlayers(maxPlayers: Int): ProcessUpdateRequest

    /**
     * Sets whether this process is visible. For example on signs.
     */
    fun setVisible(visible: Boolean): ProcessUpdateRequest

}