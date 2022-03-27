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