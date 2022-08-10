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

import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.distribution.api.Distribution
import app.simplecloud.simplecloud.distribution.api.DistributionFactory
import app.simplecloud.simplecloud.distribution.test.TestDistributionFactoryImpl
import app.simplecloud.simplecloud.distribution.test.VirtualNetwork
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

/**
 * Date: 03.08.22
 * Time: 21:13
 * @author Frederick Baier
 *
 */
class DistributionScheduleExecutorServiceTest {

    private lateinit var factory: DistributionFactory

    private var server: Distribution? = null
    private var client: Distribution? = null

    @BeforeEach
    fun setUp() {
        this.factory = TestDistributionFactoryImpl()
        this.server = this.factory.createServer(1630, emptyList())
        this.client = this.factory.createClient(Address("127.0.0.1", 1630))
    }

    @AfterEach
    fun tearDown() {
        VirtualNetwork.reset()
    }

    @Test
    fun taskStartedOnServer_willBeVisibleOnClient() {
        val scheduler = this.server!!.getScheduler("test")
        val countingRunnable = CountingRunnable()
        scheduler.scheduleAtFixedRate(countingRunnable, 1, 1, TimeUnit.SECONDS)
        val clientSideScheduler = this.client!!.getScheduler("test")
        Assertions.assertEquals(1, clientSideScheduler.getScheduledTasks().size)
    }

    @Test
    fun taskStartedOnServer_unregisterOnClient_willBeGoneInClientAndServer() {
        val scheduler = this.server!!.getScheduler("test")
        val countingRunnable = CountingRunnable()
        scheduler.scheduleAtFixedRate(countingRunnable, 1, 1, TimeUnit.SECONDS)
        val clientScheduler = this.client!!.getScheduler("test")
        val task = clientScheduler.getScheduledTasks()[0]
        clientScheduler.removeTask(task)
        Assertions.assertEquals(0, clientScheduler.getScheduledTasks().size)
        Assertions.assertEquals(0, scheduler.getScheduledTasks().size)
    }

    @Test
    fun taskStartedOnClient_clientShutdown_taskWillContinue() {
        val clientScheduler = this.client!!.getScheduler("test")
        val countingRunnable = CountingRunnable()
        val task = clientScheduler.scheduleAtFixedRate(countingRunnable, 1, 1, TimeUnit.SECONDS)
        this.client!!.shutdown()
        Thread.sleep(1_010)
        Assertions.assertEquals(1, countingRunnable.count)
    }

    @Test
    fun afterSchedulerShutdown_TasksWillNoLongerBeExecuted() {
        val serverScheduler = this.server!!.getScheduler("test")
        val countingRunnable = CountingRunnable()
        serverScheduler.scheduleAtFixedRate(countingRunnable, 1, 1, TimeUnit.SECONDS)
        serverScheduler.shutdown()
        Thread.sleep(1_010)
        Assertions.assertEquals(0, countingRunnable.count)
    }

    @Test
    fun serverShutdown_countdownWillStop() {
        val serverScheduler = this.server!!.getScheduler("test")
        val countingRunnable = CountingRunnable()
        serverScheduler.scheduleAtFixedRate(countingRunnable, 1, 1, TimeUnit.SECONDS)
        this.server!!.shutdown()
        Thread.sleep(1_010)
        Assertions.assertEquals(0, countingRunnable.count)
    }

    @Test
    fun oneServerShutdown_OneStillRunning_SchedulersWillNotShutdown() {
        val server2 = this.factory.createServer(1631, listOf(Address("127.0.0.1", 1630)))
        val serverScheduler = this.server!!.getScheduler("test")
        val countingRunnable = CountingRunnable()
        val task = serverScheduler.scheduleAtFixedRate(countingRunnable, 1, 1, TimeUnit.SECONDS)
        this.server!!.shutdown()
        Thread.sleep(1_010)
        Assertions.assertEquals(1, countingRunnable.count)
    }

    @Test
    fun twoServerAndOneClient_BothServerShutdown_SchedulerWillShutdown() {
        val server2 = this.factory.createServer(1631, listOf(Address("127.0.0.1", 1630)))
        val serverScheduler = this.server!!.getScheduler("test")
        val countingRunnable = CountingRunnable()
        val task = serverScheduler.scheduleAtFixedRate(countingRunnable, 1, 1, TimeUnit.SECONDS)
        this.server!!.shutdown()
        server2.shutdown()
        Thread.sleep(1_010)
        Assertions.assertEquals(0, countingRunnable.count)
    }
    //test schedule after all component shutdown
    //test scheduler shutdown

}