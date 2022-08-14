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

package app.simplecloud.simplecloud.distribution.hazelcast

import app.simplecloud.simplecloud.distribution.api.Distribution
import app.simplecloud.simplecloud.distribution.api.DistributionAware
import app.simplecloud.simplecloud.distribution.api.ScheduledExecutorService
import app.simplecloud.simplecloud.distribution.api.ScheduledTask
import com.hazelcast.core.HazelcastInstance
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/**
 * Date: 10.08.22
 * Time: 09:06
 * @author Frederick Baier
 *
 */
class HazelCastScheduledExecutorService(
    private val name: String,
    private val hazelCast: HazelcastInstance,
    private var distribution: Distribution,
) : ScheduledExecutorService {

    private val hazelCastScheduler = this.hazelCast.getScheduledExecutorService(name)

    override fun scheduleAtFixedRate(
        runnable: Runnable,
        initialDelay: Int,
        period: Int,
        timeUnit: TimeUnit,
    ): ScheduledTask {
        if (runnable is DistributionAware) {
            runnable.setDistribution(this.distribution)
        }
        val scheduledFuture =
            hazelCastScheduler.scheduleAtFixedRate<Unit>(runnable, initialDelay.toLong(), period.toLong(), timeUnit)
        return HazelCastScheduledTask(scheduledFuture)
    }

    override fun cancelTask(scheduledTask: ScheduledTask) {
        if (scheduledTask is HazelCastScheduledTask) {
            val future = scheduledTask.future
            future.cancel(false)
            future.dispose()
        }
    }

    override fun getScheduledTasks(): CompletableFuture<List<ScheduledTask>> {
        return CompletableFuture.supplyAsync {
            val allScheduledFutures = this.hazelCastScheduler.getAllScheduledFutures<Unit>()
            return@supplyAsync allScheduledFutures.values.flatten().map { HazelCastScheduledTask(it) }
        }.orTimeout(500, TimeUnit.MILLISECONDS) as CompletableFuture<List<ScheduledTask>>
    }

    override fun shutdown() {
        this.hazelCastScheduler.shutdown()
    }

}