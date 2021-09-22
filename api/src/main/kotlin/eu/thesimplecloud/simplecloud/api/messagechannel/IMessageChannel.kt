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

package eu.thesimplecloud.simplecloud.api.messagechannel

import eu.thesimplecloud.simplecloud.api.messagechannel.handler.IMessageHandler
import eu.thesimplecloud.simplecloud.api.utils.INameable
import eu.thesimplecloud.simplecloud.api.utils.INetworkComponent
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
interface IMessageChannel<T : Any, R: Any> : INameable {

    /**
     * Creates a message request
     * @param message the message to be sent
     * @param receiver the receiver of the message
     * @return the created message request with the expected response type
     */
    fun createMessageRequest(message: T, receiver: INetworkComponent): IMessageRequest<R>

    /**
     * Creates a message request
     * @param message the message to be sent
     * @param receivers the receivers of the message
     * @return the created message request with no response
     */
    fun createMessageRequest(message: T, receivers: List<INetworkComponent>): IMessageRequest<Unit>

    /**
     * Sets the message handler to be used for incoming messages
     * The handler is notified every time a message is received
     * @param handler the message handler to handle incoming messages
     */
    fun setMessageHandler(handler: IMessageHandler<T, R>)

    /**
     * Handles a request
     */
    fun handleRequest(message: T, sender: INetworkComponent): CompletableFuture<R>

}