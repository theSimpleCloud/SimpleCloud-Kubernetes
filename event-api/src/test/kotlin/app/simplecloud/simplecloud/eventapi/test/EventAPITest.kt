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

import app.simplecloud.simplecloud.eventapi.*
import org.junit.jupiter.api.BeforeEach

/**
 * Date: 14.05.22
 * Time: 12:39
 * @author Frederick Baier
 *
 */
open class EventAPITest {

    protected lateinit var eventManager: EventManager

    protected lateinit var eventRegisterer: EventRegisterer

    @BeforeEach
    open fun setUp() {
        this.eventManager = DefaultEventManager()
        this.eventRegisterer = object : EventRegisterer {}
    }

    class TestEvent : Event {

    }

    class TestEventListener : Listener {

        var wasEventCalled = false
            private set

        @CloudEventHandler
        fun on(event: TestEvent) {
            this.wasEventCalled = true
        }

    }

    class FailingEventListener : Listener {

        @CloudEventHandler
        fun on(event: TestEvent) {
            throw RuntimeException("FailingEventListener")
        }

    }

}