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

package app.simplecloud.simplecloud.api.impl.repository.ignite.message

import app.simplecloud.simplecloud.api.future.unitFuture
import app.simplecloud.simplecloud.api.messagechannel.handler.UnitMessageHandler
import app.simplecloud.simplecloud.api.messagechannel.manager.MessageChannelManager
import app.simplecloud.simplecloud.api.utils.NetworkComponent
import com.google.inject.Inject
import com.google.inject.Singleton
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Date: 28.03.22
 * Time: 18:34
 * @author Frederick Baier
 *
 */
@Singleton
class IgniteCacheUpdateMessaging @Inject constructor(
    private val messageChannelManager: MessageChannelManager
) : UnitMessageHandler<IgniteCacheUpdateMessageDto<Any>> {

    private val messageChannel = this.messageChannelManager
        .getOrCreateMessageChannel<IgniteCacheUpdateMessageDto<Any>, Unit>("internal_cache_update")

    private val listeners = CopyOnWriteArrayList<ListenerData>()

    init {
        this.messageChannel.setMessageHandler(this)
    }

    fun sendMessage(igniteCacheUpdateMessageDto: IgniteCacheUpdateMessageDto<Any>) {
        this.messageChannel.createMessageRequestToAll(igniteCacheUpdateMessageDto).submit()
    }

    override fun handleMessage(
        message: IgniteCacheUpdateMessageDto<Any>,
        sender: NetworkComponent
    ): CompletableFuture<Unit> {
        val cacheName = message.cacheName
        val listenersToInvoke = this.listeners.filter { it.cacheName == cacheName }
        listenersToInvoke.forEach { it.listener.messageReceived(message.action, message.key) }
        return unitFuture()
    }

    fun registerListener(cacheName: String, listener: Listener<out Any>) {
        this.listeners.add(ListenerData(cacheName, listener as Listener<Any>))
    }

    class ListenerData(
        val cacheName: String,
        val listener: Listener<Any>
    )

    interface Listener<K> {
        /**
         * @param action
         * @param key the key of the entry updated in the cache
         */
        fun messageReceived(action: CacheAction, key: K)
    }

}