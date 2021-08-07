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

package eu.thesimplecloud.simplecloud.task

/**
 * Created by IntelliJ IDEA.
 * Date: 02/08/2021
 * Time: 11:07
 * @author Frederick Baier
 */
class Timing {

    private var startTime: Long? = null
    private var stopTime: Long? = null

    /**
     * Starts the time measuring
     */
    fun start() {
        this.startTime = System.currentTimeMillis()
    }

    /**
     * Stops the time measuring
     */
    fun stop() {
        this.stopTime = System.currentTimeMillis()
    }

    /**
     * Returns the time between the start call and the stop call
     * If stop was not called then between the start call and now
     */
    fun getTime(): Long {
        val startTime = this.startTime
        val stopTime = this.stopTime
        if (startTime == null) {
            throw IllegalStateException("Start must be called before getting the time measured")
        }
        return (stopTime ?: System.currentTimeMillis()) - startTime
    }

}