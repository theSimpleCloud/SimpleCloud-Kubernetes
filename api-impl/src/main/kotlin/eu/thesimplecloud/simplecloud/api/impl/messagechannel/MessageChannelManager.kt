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

import com.google.inject.Inject
import com.google.inject.Singleton
import eu.thesimplecloud.simplecloud.api.impl.ignite.IgniteQueryHandler
import eu.thesimplecloud.simplecloud.api.messagechannel.IMessageChannel
import eu.thesimplecloud.simplecloud.api.messagechannel.manager.IMessageChannelManager
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by IntelliJ IDEA.
 * Date: 29.05.2021
 * Time: 10:47
 * @author Frederick Baier
 */
@Singleton
class MessageChannelManager @Inject constructor(
    private val queryHandler: IgniteQueryHandler
) : IMessageChannelManager {

    private val registeredMessageChannels = CopyOnWriteArrayList<MessageChannel<*, *>>()

    override fun <T : Any, R : Any> registerMessageChannel(name: String): IMessageChannel<T, R> {
        val messageChannel = MessageChannel<T, R>(name, queryHandler)
        this.registeredMessageChannels.add(messageChannel)
        return messageChannel
    }

    override fun <T : Any, R : Any> getMessageChannelByName(name: String): IMessageChannel<T, R>? {
        return getMessageChannelByNameInternal(name) as IMessageChannel<T, R>?
    }

    private fun getMessageChannelByNameInternal(name: String): MessageChannel<*, *>? {
        return this.registeredMessageChannels.firstOrNull { it.getName() == name }
    }

    override fun unregisterMessageChannel(name: String) {
        val messageChannel = getMessageChannelByNameInternal(name)
    }
}