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

package app.simplecloud.simplecloud.distibution.hazelcast

import app.simplecloud.simplecloud.distribution.api.DistributionComponent
import app.simplecloud.simplecloud.distribution.api.MessageListener
import app.simplecloud.simplecloud.distribution.api.MessageManager
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.topic.ITopic

class HazelCastMessageManager(
    private val selfComponent: DistributionComponent,
    private val hazelCast: HazelcastInstance
) : MessageManager {

    @Volatile
    private var messageListener: MessageListener = object : MessageListener {
        override fun messageReceived(message: Any, sender: DistributionComponent) {
        }
    }

    init {
        createAddressedMessageListener()
        createAllMessageListener()
    }

    override fun sendMessage(any: Any) {
        this.hazelCast.getTopic<HazelCastPacket>("all")
            .publish(HazelCastPacket(this.selfComponent, any))
    }

    override fun sendMessage(any: Any, receiver: DistributionComponent) {
        this.hazelCast.getTopic<HazelCastPacket>(receiver.getDistributionId().toString())
            .publish(HazelCastPacket(this.selfComponent, any))
    }

    override fun setMessageListener(messageListener: MessageListener) {
        this.messageListener = messageListener
    }

    private fun createAddressedMessageListener() {
        val topic = this.hazelCast.getTopic<HazelCastPacket>(this.selfComponent.getDistributionId().toString())
        addListenerToTopic(topic)
    }

    private fun createAllMessageListener() {
        val topic = this.hazelCast.getTopic<HazelCastPacket>("all")
        addListenerToTopic(topic)
    }

    private fun addListenerToTopic(topic: ITopic<HazelCastPacket>) {
        topic.addMessageListener {
            val packet = it.messageObject
            this.messageListener.messageReceived(packet.message, packet.sender)
        }
    }

}
