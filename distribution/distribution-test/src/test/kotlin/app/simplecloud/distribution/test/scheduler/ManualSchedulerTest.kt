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

package app.simplecloud.distribution.test.scheduler

import app.simplecloud.simplecloud.distribution.test.scheduler.TestManualScheduledExecutorService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit


/**
 * Date: 05.08.22
 * Time: 09:48
 * @author Frederick Baier
 *
 */
class ManualSchedulerTest {

    private var scheduler: TestManualScheduledExecutorService = TestManualScheduledExecutorService()


    @BeforeEach
    fun setUp() {
        this.scheduler = TestManualScheduledExecutorService()
    }

    @Test
    fun simpleTaskCallAfter0InitialDelay() {
        val runnable = CalledRunnable()
        scheduler.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS)
        scheduler.executeTick()
        Assertions.assertTrue(runnable.wasCalled)
    }

    @Test
    fun simpleTaskCallAfter1SecondInitialDelay() {
        val runnable = CalledRunnable()
        scheduler.scheduleAtFixedRate(runnable, 1, 1, TimeUnit.SECONDS)
        scheduler.executeTick()
        Assertions.assertFalse(runnable.wasCalled)
        scheduler.skip(1, TimeUnit.SECONDS)
        scheduler.executeTick()
        Assertions.assertTrue(runnable.wasCalled)
    }

    @Test
    fun schedule2TasksWith0InitialDelay_bothWillBeExecutedOnNextTick() {
        val runnable1 = CalledRunnable()
        val runnable2 = CalledRunnable()
        scheduler.scheduleAtFixedRate(runnable1, 0, 1, TimeUnit.SECONDS)
        scheduler.scheduleAtFixedRate(runnable2, 0, 1, TimeUnit.SECONDS)
        scheduler.executeTick()
        Assertions.assertTrue(runnable1.wasCalled)
        Assertions.assertTrue(runnable2.wasCalled)
    }

    @Test
    fun schedule2TasksWith1InitialDelay_bothWillBeExecutedAfterOneSecond() {
        val runnable1 = CalledRunnable()
        val runnable2 = CalledRunnable()
        scheduler.scheduleAtFixedRate(runnable1, 1, 1, TimeUnit.SECONDS)
        scheduler.scheduleAtFixedRate(runnable2, 1, 1, TimeUnit.SECONDS)
        scheduler.executeTick()
        Assertions.assertFalse(runnable1.wasCalled)
        Assertions.assertFalse(runnable2.wasCalled)
        scheduler.skip(1, TimeUnit.SECONDS)
        scheduler.executeTick()
        Assertions.assertTrue(runnable1.wasCalled)
        Assertions.assertTrue(runnable2.wasCalled)
    }

    @Test
    fun taskWith0InitialDelay_willNotBeExecutedTwice() {
        val runnable = CountingRunnable()
        scheduler.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS)
        scheduler.executeTick()
        scheduler.executeTick()
        Assertions.assertEquals(1, runnable.count)

    }

    @Test
    fun taskWith1Period_willBeExecutedAgainAfterOneSecond() {
        val runnable = CountingRunnable()
        scheduler.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS)
        scheduler.executeTick()
        Assertions.assertEquals(1, runnable.count)
        scheduler.skip(1, TimeUnit.SECONDS)
        scheduler.executeTick()
        Assertions.assertEquals(2, runnable.count)
    }

    @Test
    fun taskWillNotBeCalledAfterUnregister() {
        val runnable = CountingRunnable()
        val scheduledTask = scheduler.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS)
        scheduler.executeTick()
        scheduler.removeTask(scheduledTask)
        scheduler.skip(1, TimeUnit.SECONDS)
        scheduler.executeTick()
        Assertions.assertEquals(1, runnable.count)
    }

    @Test
    fun afterTaskScheduled_TaskWillBeRegistered() {
        this.scheduler.scheduleAtFixedRate(CountingRunnable(), 1, 1, TimeUnit.SECONDS)
        Assertions.assertEquals(1, this.scheduler.getScheduledTasks().size)
    }

    @Test
    fun afterTaskScheduled_TaskWillBeRegistered_2() {
        this.scheduler.scheduleAtFixedRate(CountingRunnable(), 1, 1, TimeUnit.SECONDS)
        this.scheduler.scheduleAtFixedRate(CountingRunnable(), 1, 1, TimeUnit.SECONDS)
        Assertions.assertEquals(2, this.scheduler.getScheduledTasks().size)
    }

    @Test
    fun registerTwoTasks_unregisterOne_OneTaskWillBeRegistered() {
        this.scheduler.scheduleAtFixedRate(CountingRunnable(), 1, 1, TimeUnit.SECONDS)
        val task = this.scheduler.scheduleAtFixedRate(CountingRunnable(), 1, 1, TimeUnit.SECONDS)
        this.scheduler.removeTask(task)
        Assertions.assertEquals(1, this.scheduler.getScheduledTasks().size)
    }

}