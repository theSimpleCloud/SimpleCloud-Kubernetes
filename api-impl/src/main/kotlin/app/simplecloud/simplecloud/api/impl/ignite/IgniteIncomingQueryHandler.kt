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

package app.simplecloud.simplecloud.api.impl.ignite

import app.simplecloud.simplecloud.api.messagechannel.MessageChannel
import app.simplecloud.simplecloud.api.messagechannel.manager.MessageChannelManager
import app.simplecloud.simplecloud.api.utils.NetworkComponent
import java.util.concurrent.CompletableFuture

class IgniteIncomingQueryHandler(
    private val igniteQueryHandler: IgniteQueryHandler,
    private val messageChannelManager: MessageChannelManager,
    private val networkComponent: NetworkComponent,
    private val queryObject: IgniteDataTransferObject,
) {
    fun handle() {
        val channel = getMessageChannelByName(this.queryObject.topic)
        val resultFuture = channel.handleRequest(this.queryObject.message.getOrThrow(), this.networkComponent)
        handleResult(resultFuture)
    }

    private fun getMessageChannelByName(name: String): MessageChannel<Any, Any> {
        return this.messageChannelManager.getMessageChannelByName<Any, Any>(name)
            ?: throw NoSuchElementException("Cannot find message channel by name: $name")
    }

    private fun handleResult(resultFuture: CompletableFuture<Any>) {
        resultFuture.handle { result, exception ->
            if (exception != null) {
                handleFutureResult(Result.failure(exception))
            } else {
                handleFutureResult(Result.success(result))
            }
        }
    }

    private fun handleFutureResult(result: Result<Any>) {
        this.igniteQueryHandler.sendPacket(
            createResultResponseObject(result),
            this.networkComponent.getIgniteId()
        )
    }

    private fun createResultResponseObject(result: Result<Any>): IgniteDataTransferObject {
        return IgniteDataTransferObject(this.queryObject.topic, this.queryObject.messageId, result, true)
    }

}