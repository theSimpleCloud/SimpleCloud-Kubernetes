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

import app.simplecloud.simplecloud.distribution.test.scheduler.TestThreadScheduledExecutorService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

/**
 * Date: 06.08.22
 * Time: 09:12
 * @author Frederick Baier
 *
 */
class ThreadSchedulerTest {

    private var scheduler: TestThreadScheduledExecutorService = TestThreadScheduledExecutorService()


    @BeforeEach
    fun setUp() {
        this.scheduler = TestThreadScheduledExecutorService()
    }

    @Test
    fun willExecuteAfterOneSecondInitialDelay() {
        val countingRunnable = CountingRunnable()
        val scheduledTask = scheduler.scheduleAtFixedRate(countingRunnable, 1, 1, TimeUnit.SECONDS)
        Assertions.assertEquals(0, countingRunnable.count)
        Thread.sleep(1_010)
        Assertions.assertEquals(1, countingRunnable.count)
    }

    @Test
    fun willExecuteAfterOneSecondInitialDelayAndAfterOneSecondPeriod() {
        val countingRunnable = CountingRunnable()
        val scheduledTask = scheduler.scheduleAtFixedRate(countingRunnable, 1, 1, TimeUnit.SECONDS)
        Assertions.assertEquals(0, countingRunnable.count)
        Thread.sleep(2_010)
        Assertions.assertEquals(2, countingRunnable.count)
    }

    @Test
    fun willNotExecuteAfterUnregister() {
        val countingRunnable = CountingRunnable()
        val scheduledTask = scheduler.scheduleAtFixedRate(countingRunnable, 1, 1, TimeUnit.SECONDS)
        Thread.sleep(1_010)
        scheduler.removeTask(scheduledTask)
        Thread.sleep(1_010)
        Assertions.assertEquals(1, countingRunnable.count)
    }

    @Test
    fun schedulerWith5SecondPeriod_willNotBlockOthers() {
        val countingRunnable = CountingRunnable()
        val countingRunnable2 = CountingRunnable()
        scheduler.scheduleAtFixedRate(countingRunnable, 1, 5, TimeUnit.SECONDS)
        Thread.sleep(1_010)
        scheduler.scheduleAtFixedRate(countingRunnable2, 1, 1, TimeUnit.SECONDS)
        Thread.sleep(2_010)
        Assertions.assertEquals(2, countingRunnable2.count)
    }

    @Test
    fun afterShutdown_thereWillBeNoFurtherExecutes() {
        val countingRunnable = CountingRunnable()
        scheduler.scheduleAtFixedRate(countingRunnable, 1, 1, TimeUnit.SECONDS)
        scheduler.shutdown()
        Thread.sleep(1_010)
        Assertions.assertEquals(0, countingRunnable.count)
    }

}