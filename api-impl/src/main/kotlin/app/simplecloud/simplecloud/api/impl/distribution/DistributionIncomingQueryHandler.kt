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

package app.simplecloud.simplecloud.api.impl.distribution

import app.simplecloud.simplecloud.api.messagechannel.MessageChannel
import app.simplecloud.simplecloud.api.messagechannel.manager.MessageChannelManager
import app.simplecloud.simplecloud.api.utils.NetworkComponent
import java.util.concurrent.CompletableFuture

class DistributionIncomingQueryHandler(
    private val distributedQueryHandler: DistributedQueryHandlerImpl,
    private val messageChannelManager: MessageChannelManager,
    private val sender: NetworkComponent,
    private val queryObject: DistributionDataTransferObject,
) {
    fun handle() {
        val channel = getMessageChannelByName(this.queryObject.topic)
        val resultFuture = channel.handleRequest(this.queryObject.message.getOrThrow(), this.sender)
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
        this.distributedQueryHandler.sendPacketToSingleReceiver(
            createResultResponseObject(result),
            this.sender
        )
    }

    private fun createResultResponseObject(result: Result<Any>): DistributionDataTransferObject {
        return DistributionDataTransferObject(this.queryObject.topic, this.queryObject.messageId, result, true)
    }

}