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

package app.simplecloud.simplecloud.node.task

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.utils.CloudState
import app.simplecloud.simplecloud.distribution.api.Distribution
import app.simplecloud.simplecloud.distribution.api.DistributionAware
import app.simplecloud.simplecloud.module.api.impl.NodeCloudAPIImpl
import kotlinx.coroutines.runBlocking

/**
 * Date: 15.08.22
 * Time: 17:30
 * @author Frederick Baier
 *
 */
class NodeOnlineProcessCheckerRunnable : Runnable, DistributionAware {

    @Transient
    @Volatile
    private var cloudAPI: NodeCloudAPIImpl? = null

    override fun run() {
        val cloudAPI = this.cloudAPI ?: return

        runBlocking {
            if (cloudAPI.getCloudStateService().getCloudState().await() == CloudState.DISABLED)
                return@runBlocking
            NodeOnlineProcessHandler(cloudAPI).handleProcesses()
        }
    }

    override fun setDistribution(distribution: Distribution) {
        val userContext = distribution.getUserContext()
        this.cloudAPI = userContext["cloudAPI"] as NodeCloudAPIImpl
    }
}