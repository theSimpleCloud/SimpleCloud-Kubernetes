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

import app.simplecloud.simplecloud.api.impl.distribution.DistributedQueryHandlerImpl
import app.simplecloud.simplecloud.api.messagechannel.MessageChannel
import app.simplecloud.simplecloud.api.messagechannel.manager.MessageChannelManager
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.api.service.NodeService
import app.simplecloud.simplecloud.distribution.api.Distribution
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by IntelliJ IDEA.
 * Date: 29.05.2021
 * Time: 10:47
 * @author Frederick Baier
 */
class MessageChannelManagerImpl(
    private val nodeService: NodeService,
    private val processService: CloudProcessService,
    private val distribution: Distribution
) : MessageChannelManager {

    private val queryHandler =
        DistributedQueryHandlerImpl(this, this.nodeService, this.processService, this.distribution)

    private val registeredMessageChannels = CopyOnWriteArrayList<MessageChannel<*, *>>()

    override fun <T : Any, R : Any> registerMessageChannel(name: String): MessageChannel<T, R> {
        val messageChannel = MessageChannelImpl<T, R>(name, this.queryHandler)
        this.registeredMessageChannels.add(messageChannel)
        return messageChannel
    }

    override fun <T : Any, R : Any> getMessageChannelByName(name: String): MessageChannel<T, R>? {
        return getMessageChannelByNameInternal(name) as MessageChannel<T, R>?
    }

    override fun <T : Any, R : Any> getOrCreateMessageChannel(name: String): MessageChannel<T, R> {
        val messageChannelByName = getMessageChannelByName<T, R>(name)
        return messageChannelByName ?: registerMessageChannel(name)
    }

    override fun unregisterMessageChannel(name: String) {
        val messageChannel = getMessageChannelByNameInternal(name)
        this.registeredMessageChannels.remove(messageChannel)
    }

    private fun getMessageChannelByNameInternal(name: String): MessageChannel<*, *>? {
        return this.registeredMessageChannels.firstOrNull { it.getName() == name }
    }
}