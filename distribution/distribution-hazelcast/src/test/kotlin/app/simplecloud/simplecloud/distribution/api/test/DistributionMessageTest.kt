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

import app.simplecloud.simplecloud.distibution.hazelcast.HazelcastDistributionFactory
import app.simplecloud.simplecloud.distribution.api.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

/**
 * Date: 08.04.22
 * Time: 17:56
 * @author Frederick Baier
 *
 */
@Disabled
class DistributionMessageTest {

    private lateinit var factory: DistributionFactory

    private var server: Distribution? = null
    private var client: Distribution? = null

    @BeforeEach
    fun setUp() {
        this.factory = HazelcastDistributionFactory()
    }

    @AfterEach
    fun tearDown() {
        server?.shutdown()
        client?.shutdown()
    }

    @Test
    fun newServer_receiveSelfMessages() {
        this.server = this.factory.createServer(1630, emptyList())
        val messageManager = server!!.getMessageManager()
        var messageReceived = false
        messageManager.setMessageListener(object : MessageListener {

            override fun messageReceived(message: Any, sender: NetworkComponent) {
                messageReceived = true
            }

        })
        messageManager.sendMessage("test")
        Thread.sleep(500)
        assertTrue(messageReceived)
    }

    @Test
    fun serverAndClient_ClientReceiveMessage() {
        this.server = this.factory.createServer(1630, emptyList())
        this.client = this.factory.createClient(Address("127.0.0.1", 1630))
        val clientMessageManager = client!!.getMessageManager()
        var received = 0
        clientMessageManager.setMessageListener(object : MessageListener {

            override fun messageReceived(message: Any, sender: NetworkComponent) {
                received++
            }

        })
        val serverMessageManager = server!!.getMessageManager()
        serverMessageManager.setMessageListener(object : MessageListener {

            override fun messageReceived(message: Any, sender: NetworkComponent) {
                received++
            }

        })
        serverMessageManager.sendMessage("test")
        Thread.sleep(1000)
        assertEquals(2, received)
    }

    @Test
    fun singleReceiverMessage() {
        this.server = this.factory.createServer(1630, emptyList())
        this.client = this.factory.createClient(Address("127.0.0.1", 1630))
        val clientMessageManager = client!!.getMessageManager()
        var received = 0
        clientMessageManager.setMessageListener(object : MessageListener {

            override fun messageReceived(message: Any, sender: NetworkComponent) {
                received++
            }

        })
        val serverMessageManager = server!!.getMessageManager()
        serverMessageManager.setMessageListener(object : MessageListener {

            override fun messageReceived(message: Any, sender: NetworkComponent) {
                received++
            }

        })
        serverMessageManager.sendMessage("test", client!!.getSelfComponent())
        Thread.sleep(500)
        assertEquals(1, received)
    }

}