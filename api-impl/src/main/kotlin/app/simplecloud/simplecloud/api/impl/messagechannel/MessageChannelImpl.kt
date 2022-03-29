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

package app.simplecloud.simplecloud.api.impl.messagechannel

import app.simplecloud.simplecloud.api.impl.ignite.IgniteQueryHandler
import app.simplecloud.simplecloud.api.impl.messagechannel.request.AllReceiverMessageRequest
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

    override fun createMessageRequestToAll(message: T): MessageRequest<Unit> {
        return AllReceiverMessageRequest(this.name, message, queryHandler)
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