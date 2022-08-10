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

import java.util.concurrent.TimeUnit

/**
 * Date: 05.08.22
 * Time: 09:58
 * @author Frederick Baier
 *
 */
class FixedRateRepeatingTask(
    val runnable: Runnable,
    initialDelay: Int,
    val period: Int,
    val timeUnit: TimeUnit,
    initialCurrentTime: Long,
) : InternalScheduledTask {

    @Volatile
    private var nextExecutionTimeStamp = initialCurrentTime + this.timeUnit.toMillis(initialDelay.toLong())

    override fun executeTask() {
        runnable.run()
    }

    override fun recalculateNextExecutionTimeStamp(currentTime: Long) {
        this.nextExecutionTimeStamp = currentTime + this.timeUnit.toMillis(this.period.toLong())
    }

    override fun getNextExecutionTimeStamp(): Long {
        return this.nextExecutionTimeStamp
    }
}