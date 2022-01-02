/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package app.simplecloud.simplecloud.api.request.process

import app.simplecloud.simplecloud.api.image.Image
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
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
    fun getProcessGroup(): CloudProcessGroup

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
     * e.g: Lobby-2 -> 2 is the procoess number
     * @return this
     */
    fun setProcessNumber(number: Int): ProcessStartRequest

    /**
     * Sets the image for the new process
     * @return this
     */
    fun setImage(image: Image): ProcessStartRequest

}