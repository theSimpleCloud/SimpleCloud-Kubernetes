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

import app.simplecloud.simplecloud.api.image.Image
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.template.ProcessTemplate
import app.simplecloud.simplecloud.api.utils.Request

/**
 * Created by IntelliJ IDEA.
 * Date: 16.03.2021
 * Time: 20:09
 * @author Frederick Baier
 *
 * Used to configure a process before starting it
 *
 */
interface ProcessStartRequest : Request<CloudProcess> {

    /**
     * Returns the process group this request will start a process of
     */
    fun getProcessTemplate(): ProcessTemplate

    /**
     * Sets the max players for the new process
     * @return this
     */
    fun setMaxPlayers(maxPlayers: Int): ProcessStartRequest

    /**
     * Sets the max memory for the new process
     * @return this
     */
    fun setMaxMemory(memory: Int): ProcessStartRequest

    /**
     * Sets the number of the new process
     * e.g: Lobby-2 -> 2 is the process number
     * @return this
     */
    fun setProcessNumber(number: Int): ProcessStartRequest

    /**
     * Sets the image for the new process
     * @return this
     */
    fun setImage(image: Image): ProcessStartRequest

}