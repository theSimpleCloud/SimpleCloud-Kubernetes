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

package app.simplecloud.simplecloud.api.impl.messagechannel

import app.simplecloud.simplecloud.api.impl.ignite.IgniteQueryHandler
import app.simplecloud.simplecloud.api.impl.ignite.IgniteQueryHandlerImpl
import app.simplecloud.simplecloud.api.impl.messagechannel.request.MultipleReceiverMessageRequest
import app.simplecloud.simplecloud.api.impl.messagechannel.request.SingleReceiverMessageRequest
import app.simplecloud.simplecloud.api.messagechannel.MessageChannel
import app.simplecloud.simplecloud.api.messagechannel.MessageRequest
import app.simplecloud.simplecloud.api.messagechannel.handler.MessageHandler
import app.simplecloud.simplecloud.api.utils.NetworkComponent
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 29.05.2021
 * Time: 10:47
 * @author Frederick Baier
 */
class MessageChannelImpl<T : Any, R : Any>(
    private val name: String,
    private val queryHandler: IgniteQueryHandler
) : MessageChannel<T, R> {

    @Volatile
    private var messageHandler: MessageHandler<T, R> = EmptyMessageHandler<T, R>()

    override fun createMessageRequest(message: T, receiver: NetworkComponent): MessageRequest<R> {
        return SingleReceiverMessageRequest<R>(this.name, message, receiver, queryHandler)
    }

    override fun createMessageRequest(message: T, receivers: List<NetworkComponent>): MessageRequest<Unit> {
        return MultipleReceiverMessageRequest(this.name, message, receivers, queryHandler)
    }

    override fun setMessageHandler(handler: MessageHandler<T, R>) {
        this.messageHandler = handler
    }

    override fun getName(): String {
        return this.name
    }

    override fun handleRequest(message: T, sender: NetworkComponent): CompletableFuture<R> {
        return this.messageHandler.handleMessage(message, sender)
    }
}