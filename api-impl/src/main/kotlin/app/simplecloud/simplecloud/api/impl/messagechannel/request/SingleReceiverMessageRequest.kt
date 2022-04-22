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

package app.simplecloud.simplecloud.api.impl.messagechannel.request

import app.simplecloud.simplecloud.api.impl.distribution.DistributedQueryHandler
import app.simplecloud.simplecloud.api.messagechannel.MessageRequest
import app.simplecloud.simplecloud.api.utils.NetworkComponent
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 29.05.2021
 * Time: 10:48
 * @author Frederick Baier
 */
class SingleReceiverMessageRequest<R : Any>(
    private val topic: String,
    private val message: Any,
    private val receiver: NetworkComponent,
    private val queryHandler: DistributedQueryHandler
) : MessageRequest<R> {

    override fun submit(): CompletableFuture<R> {
        return queryHandler.sendQuery<R>(topic, message, receiver)
    }
}