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

import app.simplecloud.simplecloud.distribution.api.ScheduledExecutorService
import app.simplecloud.simplecloud.distribution.api.ScheduledTask
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit

open class TestManualScheduledExecutorService(
    private val time: Time = Time(),
) : ScheduledExecutorService {

    private val tasks = CopyOnWriteArrayList<InternalScheduledTask>()

    override fun scheduleAtFixedRate(
        runnable: Runnable,
        initialDelay: Int,
        period: Int,
        timeUnit: TimeUnit,
    ): InternalScheduledTask {
        val testScheduledTask =
            FixedRateRepeatingTask(runnable, initialDelay, period, timeUnit, this.time.currentTime())
        this.tasks.add(testScheduledTask)
        return testScheduledTask
    }

    override fun removeTask(scheduledTask: ScheduledTask) {
        this.tasks.remove(scheduledTask)
    }

    override fun shutdown() {

    }

    fun executeTick() {
        for (scheduledTask in this.tasks) {
            handleNextTask(scheduledTask)
        }
    }

    private fun handleNextTask(task: InternalScheduledTask) {
        if (task.getNextExecutionTimeStamp() <= this.time.currentTime()) {
            task.recalculateNextExecutionTimeStamp(this.time.currentTime())
            task.executeTask()
        }
    }

    fun skip(amount: Int, timeUnit: TimeUnit) {
        this.time.skip(amount, timeUnit)
    }

    override fun getScheduledTasks(): List<ScheduledTask> {
        return this.tasks
    }

}
