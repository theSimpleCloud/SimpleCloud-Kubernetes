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

package app.simplecloud.simplecloud.api.impl.messagechannel.request

import app.simplecloud.simplecloud.api.impl.ignite.IgniteQueryHandler
import app.simplecloud.simplecloud.api.impl.ignite.IgniteQueryHandlerImpl
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
    private val queryHandler: IgniteQueryHandler
) : MessageRequest<R> {

    override fun submit(): CompletableFuture<R> {
        return queryHandler.sendQuery<R>(topic, message, receiver)
    }
}