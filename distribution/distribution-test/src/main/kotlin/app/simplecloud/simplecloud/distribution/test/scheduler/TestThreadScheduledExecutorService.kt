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

package app.simplecloud.simplecloud.distribution.test.scheduler

import app.simplecloud.simplecloud.distribution.api.Distribution
import app.simplecloud.simplecloud.distribution.api.DistributionAware
import app.simplecloud.simplecloud.distribution.api.ScheduledExecutorService
import app.simplecloud.simplecloud.distribution.api.ScheduledTask
import java.util.concurrent.CompletableFuture
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

/**
 * Date: 06.08.22
 * Time: 08:20
 * @author Frederick Baier
 *
 */
class TestThreadScheduledExecutorService(
    @Volatile
    private var distribution: Distribution,
) : ScheduledExecutorService {

    private val time = Time()
    private val manualScheduler = TestManualScheduledExecutorService(time)

    private val priorityQueue = PriorityBlockingQueue<InternalScheduledTask>(1, TaskComparator())

    private val lock = ReentrantLock()
    private val lockCondition = lock.newCondition()

    private val thread: Thread

    init {
        this.thread = startThread()
    }

    fun setDistribution(distribution: Distribution) {
        this.distribution = distribution
        val scheduledTasks = getScheduledTasks().join()
        for (scheduledTask in scheduledTasks) {
            //update distribution
            if (scheduledTask is InternalScheduledTask) {
                scheduledTask.updateDistribution(distribution)
            }
        }
    }

    private fun startThread(): Thread {
        return thread {
            while (true) {
                try {
                    executeNextTaskOrWait()
                } catch (_: InterruptedException) {
                    break
                }
            }
        }
    }

    private fun executeNextTaskOrWait() {
        if (this.priorityQueue.isEmpty())
            waitUntilNotified()
        val nextTask = this.priorityQueue.peek()
        val timeLeft = getTimeLeftUntilNextExecution(nextTask)
        if (timeLeft <= 0) {
            executeTask(nextTask)
            return
        }
        waitWithLimit(timeLeft)
    }

    private fun executeTask(task: InternalScheduledTask) {
        task.recalculateNextExecutionTimeStamp(this.time.currentTime())
        task.executeTask()
        this.priorityQueue.poll()
        if (task.getNextExecutionTimeStamp() != -1L)
            this.priorityQueue.add(task)
    }

    private fun getTimeLeftUntilNextExecution(task: InternalScheduledTask): Long {
        return task.getNextExecutionTimeStamp() - this.time.currentTime()
    }

    private fun waitWithLimit(limit: Long) {
        lock.withLock {
            lockCondition.await(limit, TimeUnit.MILLISECONDS)
        }
    }

    private fun waitUntilNotified() {
        lock.withLock {
            lockCondition.await()
        }
    }

    override fun scheduleAtFixedRate(
        runnable: Runnable,
        initialDelay: Int,
        period: Int,
        timeUnit: TimeUnit,
    ): InternalScheduledTask {
        if (runnable is DistributionAware) {
            runnable.setDistribution(this.distribution)
        }
        val task = this.manualScheduler.scheduleAtFixedRate(runnable, initialDelay, period, timeUnit)
        this.priorityQueue.add(task)
        this.lock.withLock {
            this.lockCondition.signal()
        }
        return task
    }


    override fun cancelTask(scheduledTask: ScheduledTask) {
        this.manualScheduler.cancelTask(scheduledTask)
        this.priorityQueue.remove(scheduledTask)
    }

    override fun shutdown() {
        this.thread.interrupt()
    }

    override fun getScheduledTasks(): CompletableFuture<List<ScheduledTask>> {
        return this.manualScheduler.getScheduledTasks()
    }

    class TaskComparator : Comparator<InternalScheduledTask> {
        override fun compare(o1: InternalScheduledTask, o2: InternalScheduledTask): Int {
            return o1.getNextExecutionTimeStamp().compareTo(o2.getNextExecutionTimeStamp())
        }

    }

}