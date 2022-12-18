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

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.distribution.api.Distribution
import app.simplecloud.simplecloud.distribution.api.DistributionAware
import app.simplecloud.simplecloud.module.api.internal.InternalNodeCloudAPI

/**
 * Date: 18.10.22
 * Time: 12:53
 * @author Frederick Baier
 *
 */
class ErrorResolvedCheckerRunnable : Runnable, DistributionAware {

    @Transient
    @Volatile
    private var cloudAPI: InternalNodeCloudAPI? = null

    override fun run() {
        val cloudAPI = this.cloudAPI ?: return
        deleteResolvedErrors(cloudAPI)
    }

    private fun deleteResolvedErrors(cloudAPI: InternalNodeCloudAPI) = CloudScope.future {
        val errorService = cloudAPI.getErrorService()
        errorService.deleteResolvedErrors(cloudAPI)
    }

    override fun setDistribution(distribution: Distribution) {
        val userContext = distribution.getUserContext()
        this.cloudAPI = userContext["cloudAPI"] as InternalNodeCloudAPI
    }
}