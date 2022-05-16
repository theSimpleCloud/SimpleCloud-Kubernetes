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

package app.simplecloud.simplecloud.eventapi.test

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Date: 14.05.22
 * Time: 14:39
 * @author Frederick Baier
 *
 */
class EventAPIUnregisterTest : EventAPITest() {

    private lateinit var registeredListener: TestEventListener

    @BeforeEach
    override fun setUp() {
        super.setUp()
        this.registeredListener = TestEventListener()
        this.eventManager.registerListener(this.eventRegisterer, registeredListener)
    }

    @Test
    fun doNotUnregister_ListenerWillBeCalled() {
        this.eventManager.call(TestEvent())
        Assertions.assertTrue(this.registeredListener.wasEventCalled)
    }

    @Test
    fun unregisterListener_ListenerWillNotBeCalled() {
        this.eventManager.unregisterListener(this.registeredListener)
        this.eventManager.call(TestEvent())
        Assertions.assertFalse(this.registeredListener.wasEventCalled)
    }

    @Test
    fun unregisterListenerByEventRegisterer_ListenerWillNotBeCalled() {
        this.eventManager.unregisterAllListenersByRegisterer(this.eventRegisterer)
        this.eventManager.call(TestEvent())
        Assertions.assertFalse(this.registeredListener.wasEventCalled)
    }

}