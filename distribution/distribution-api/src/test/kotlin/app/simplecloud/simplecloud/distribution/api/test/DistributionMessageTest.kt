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

package app.simplecloud.simplecloud.distribution.api.test

import app.simplecloud.simplecloud.api.utils.Address
import app.simplecloud.simplecloud.distribution.api.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Date: 08.04.22
 * Time: 17:56
 * @author Frederick Baier
 *
 */
class DistributionMessageTest {

    private lateinit var factory: DistributionFactory

    @BeforeEach
    fun setUp() {
        this.factory = TestDistributionFactoryImpl()
    }

    @AfterEach
    fun tearDown() {
        VirtualNetwork.reset()
    }

    @Test
    fun newServer_receiveSelfMessages() {
        val server = this.factory.createServer(1630, emptyList())
        val messageManager = server.getMessageManager()
        var messageReceived = false
        messageManager.setMessageListener(object : MessageListener {

            override fun messageReceived(message: Any, sender: NetworkComponent) {
                messageReceived = true
            }

        })
        messageManager.sendMessage("test")

        assertTrue(messageReceived)
    }

    @Test
    fun serverAndClient_ClientReceiveMessage() {
        val server = this.factory.createServer(1630, emptyList())
        val client = this.factory.createClient(Address("127.0.0.1", 1630))
        val clientMessageManager = client.getMessageManager()
        var received = 0
        clientMessageManager.setMessageListener(object : MessageListener {

            override fun messageReceived(message: Any, sender: NetworkComponent) {
                received++
            }

        })
        val serverMessageManager = server.getMessageManager()
        serverMessageManager.setMessageListener(object : MessageListener {

            override fun messageReceived(message: Any, sender: NetworkComponent) {
                received++
            }

        })
        serverMessageManager.sendMessage("test")

        assertEquals(2, received)
    }

    @Test
    fun singleReceiverMessage() {
        val server = this.factory.createServer(1630, emptyList())
        val client = this.factory.createClient(Address("127.0.0.1", 1630))
        val clientMessageManager = client.getMessageManager()
        var received = 0
        clientMessageManager.setMessageListener(object : MessageListener {

            override fun messageReceived(message: Any, sender: NetworkComponent) {
                received++
            }

        })
        val serverMessageManager = server.getMessageManager()
        serverMessageManager.setMessageListener(object : MessageListener {

            override fun messageReceived(message: Any, sender: NetworkComponent) {
                received++
            }

        })
        serverMessageManager.sendMessage("test", client.getSelfComponent())

        assertEquals(1, received)
    }

}