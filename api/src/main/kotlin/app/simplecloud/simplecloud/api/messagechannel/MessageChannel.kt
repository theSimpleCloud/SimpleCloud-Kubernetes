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

package app.simplecloud.simplecloud.api.messagechannel

import app.simplecloud.simplecloud.api.messagechannel.handler.MessageHandler
import app.simplecloud.simplecloud.api.utils.Nameable
import app.simplecloud.simplecloud.api.utils.NetworkComponent
import app.simplecloud.simplecloud.distribution.api.DistributionComponent
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 27.03.2021
 * Time: 08:26
 * @author Frederick Baier
 *
 * @param T the type to be sent
 * @param R the type to be received
 *
 */
interface MessageChannel<T : Any, R: Any> : Nameable {

    /**
     * Creates a message request
     * @param message the message to be sent
     * @param receiver the receiver of the message
     * @return the created message request with the expected response type
     */
    fun createMessageRequest(message: T, receiver: NetworkComponent): MessageRequest<R>

    /**
     * Creates a message request
     * @param message the message to be sent
     * @param receivers the receivers of the message
     * @return the created message request with no response
     */
    fun createMessageRequest(message: T, receivers: List<NetworkComponent>): MessageRequest<Unit>

    /**
     * Creates a message request to all [DistributionComponent]s in the cluster (servers and nodes)
     * @param message the message to be sent
     * @return the created message request with no response
     */
    fun createMessageRequestToAll(message: T): MessageRequest<Unit>

    /**
     * Sets the message handler to be used for incoming messages
     * The handler is notified every time a message is received
     * @param handler the message handler to handle incoming messages
     */
    fun setMessageHandler(handler: MessageHandler<T, R>)

    /**
     * Handles a request
     */
    fun handleRequest(message: T, sender: NetworkComponent): CompletableFuture<R>

}