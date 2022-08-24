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

import app.simplecloud.simplecloud.eventapi.DefaultEventManager
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Date: 24.08.22
 * Time: 09:23
 * @author Frederick Baier
 *
 */
class EventAPIExceptionTest : EventAPITest() {

    private var exceptionHandler = ExceptionHandler()

    @BeforeEach
    override fun setUp() {
        super.setUp()
        this.exceptionHandler = ExceptionHandler()
        this.eventManager = DefaultEventManager(exceptionHandler)
    }

    @Test
    fun registerNormalListener_callEvent_NoExceptionWasThrown() {
        val failingEventListener = TestEventListener()
        this.eventManager.registerListener(eventRegisterer, failingEventListener)
        this.eventManager.call(TestEvent())
        Assertions.assertNull(this.exceptionHandler.lastException)
    }

    @Test
    fun registerFailingListener_callEvent_EventExceptionWillBeThrown() {
        val failingEventListener = FailingEventListener()
        this.eventManager.registerListener(eventRegisterer, failingEventListener)
        this.eventManager.call(TestEvent())
        Assertions.assertNotNull(this.exceptionHandler.lastException)
    }

    class ExceptionHandler : (Exception) -> Unit {

        var lastException: Exception? = null
            private set

        override fun invoke(ex: Exception) {
            this.lastException = ex
        }

    }

}