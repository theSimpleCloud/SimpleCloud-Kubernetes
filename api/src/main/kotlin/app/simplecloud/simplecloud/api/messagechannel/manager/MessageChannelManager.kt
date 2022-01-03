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

package app.simplecloud.simplecloud.api.messagechannel.manager

import app.simplecloud.simplecloud.api.messagechannel.MessageChannel

/**
 * Created by IntelliJ IDEA.
 * Date: 29.05.2021
 * Time: 09:35
 * @author Frederick Baier
 */
interface MessageChannelManager {

    /**
     * Registers and returns a new message channel
     * @param T the message type to be sent
     * @param R the type to be received
     * @param name the name of the message channel
     */
    fun <T : Any, R : Any> registerMessageChannel(name: String): MessageChannel<T, R>

    /**
     * Returns the already registered message channel found by the specified name or creates a new one
     * @param T the message type to be sent
     * @param R the type to be received
     * @param name the name of the message channel
     */
    fun <T : Any, R : Any> getOrCreateMessageChannel(name: String): MessageChannel<T, R>

    /**
     * Returns the already registered message channel found by the specified name
     * @param T the message type to be sent
     * @param R the type to be received
     * @param name the name of the message channel
     */
    fun <T : Any, R : Any> getMessageChannelByName(name: String): MessageChannel<T, R>?

    /**
     * Unregisters the message channel found by the specified [name]
     */
    fun unregisterMessageChannel(name: String)

}