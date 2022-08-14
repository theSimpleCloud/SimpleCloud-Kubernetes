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

package app.simplecloud.simplecloud.distribution.api.test.scheduler

import app.simplecloud.simplecloud.distribution.api.Cache
import app.simplecloud.simplecloud.distribution.api.Distribution
import app.simplecloud.simplecloud.distribution.api.DistributionAware

/**
 * Date: 06.08.22
 * Time: 09:14
 * @author Frederick Baier
 *
 */
class CountingRunnable() : Runnable, java.io.Serializable, DistributionAware {

    @Transient
    private var distribution: Distribution? = null

    @Transient
    private var cache: Cache<Int, Int>? = null

    override fun run() {
        val cache = cache!!
        if (cache.isEmpty()) {
            cache[0] = 1
        }
        cache[0] = cache[0]!! + 1
        println("Scheduler tick " + distribution?.getSelfComponent()?.getDistributionId() + " " + cache[0])
    }

    override fun setDistribution(distribution: Distribution) {
        this.distribution = distribution
        val cache = distribution.getOrCreateCache<Int, Int>("test")
        this.cache = cache
    }

    fun getCount(): Int {
        try {
            return this.cache!![0] ?: 0
        } catch (e: Exception) {
            return 0
        }
    }

}