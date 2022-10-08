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

package app.simplecloud.simplecloud.node.connect

import app.simplecloud.simplecloud.distribution.api.Distribution
import app.simplecloud.simplecloud.distribution.api.ServerComponent
import app.simplecloud.simplecloud.module.api.impl.PausableThreadPoolExecutor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Date: 08.10.22
 * Time: 19:17
 * @author Frederick Baier
 *
 */
class LocalModuleSchedulerWatcher(
    private val distribution: Distribution,
    private val pausableThreadPoolExecutor: PausableThreadPoolExecutor,
) {

    fun start() {
        val executor = Executors.newScheduledThreadPool(1)
        executor.scheduleAtFixedRate(createRunnable(), 0, 1, TimeUnit.SECONDS)
    }

    private fun createRunnable(): Runnable {
        return Runnable {
            updateSchedulerPauseState()
        }
    }

    private fun updateSchedulerPauseState() {
        val desiredPauseState = determineDesiredPauseState()
        pausableThreadPoolExecutor.setPaused(desiredPauseState)
    }

    private fun determineDesiredPauseState(): Boolean {
        return !shallSchedulerBeExecutedOnLocalNode()
    }

    private fun shallSchedulerBeExecutedOnLocalNode(): Boolean {
        return determineServerToExecuteSchedulerOn() == this.distribution.getSelfComponent()
    }

    private fun determineServerToExecuteSchedulerOn(): ServerComponent {
        val servers = this.distribution.getServers()
        return servers.minBy { determineServerValue(it) }
    }

    private fun determineServerValue(serverComponent: ServerComponent): Int {
        return serverComponent.getDistributionId().hashCode()
    }

}