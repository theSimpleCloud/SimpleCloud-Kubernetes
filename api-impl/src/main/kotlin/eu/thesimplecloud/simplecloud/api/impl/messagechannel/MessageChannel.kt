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

package eu.thesimplecloud.simplecloud.api.impl.messagechannel

import eu.thesimplecloud.simplecloud.api.impl.ignite.IgniteQueryHandler
import eu.thesimplecloud.simplecloud.api.impl.messagechannel.request.MultipleReceiverMessageRequest
import eu.thesimplecloud.simplecloud.api.impl.messagechannel.request.SingleReceiverMessageRequest
import eu.thesimplecloud.simplecloud.api.messagechannel.IMessageChannel
import eu.thesimplecloud.simplecloud.api.messagechannel.IMessageRequest
import eu.thesimplecloud.simplecloud.api.messagechannel.handler.IMessageHandler
import eu.thesimplecloud.simplecloud.api.utils.INetworkComponent

/**
 * Created by IntelliJ IDEA.
 * Date: 29.05.2021
 * Time: 10:47
 * @author Frederick Baier
 */
class MessageChannel<T : Any, R : Any>(
    private val name: String,
    private val queryHandler: IgniteQueryHandler
) : IMessageChannel<T, R> {

    @Volatile
    private var messageHandler: IMessageHandler<T, R> = EmptyMessageHandler<T, R>()

    override fun createMessageRequest(message: T, receiver: INetworkComponent): IMessageRequest<R> {
        return SingleReceiverMessageRequest<R>(this.name, message, receiver, queryHandler)
    }

    override fun createMessageRequest(message: T, receivers: List<INetworkComponent>): IMessageRequest<Unit> {
        return MultipleReceiverMessageRequest(this.name, message, receivers, queryHandler)
    }

    override fun setMessageHandler(handler: IMessageHandler<T, R>) {
        this.messageHandler = handler
    }

    override fun getName(): String {
        return this.name
    }

    override fun handleRequest(message: T, sender: INetworkComponent): R? {
        return this.messageHandler.handleMessage(message, sender)
    }
}