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

package app.simplecloud.simplecloud.node.process.unregister

import app.simplecloud.simplecloud.api.CloudAPI
import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedCloudProcessRepository
import app.simplecloud.simplecloud.distribution.api.Distribution
import app.simplecloud.simplecloud.distribution.api.DistributionAware
import app.simplecloud.simplecloud.kubernetes.api.KubeAPI
import app.simplecloud.simplecloud.node.connect.DistributedRepositories
import kotlinx.coroutines.launch
import java.io.Serializable

/**
 * Date: 14.08.22
 * Time: 13:57
 * @author Frederick Baier
 *
 */
class ProcessUnregisterRunnable : Runnable, Serializable, DistributionAware {

    @Transient
    @Volatile
    private var kubeAPI: KubeAPI? = null

    @Transient
    @Volatile
    private var cloudAPI: CloudAPI? = null

    @Transient
    @Volatile
    private var distributedCloudProcessRepository: DistributedCloudProcessRepository? = null

    override fun run() {
        val kubeAPI = this.kubeAPI ?: return
        val cloudAPI = this.cloudAPI ?: return
        val distributedCloudProcessRepository = this.distributedCloudProcessRepository ?: return

        CloudScope.launch {
            ProcessUnregisterExecutor(
                kubeAPI,
                cloudAPI,
                distributedCloudProcessRepository
            ).compareProcessesWithKubeAndUnregister()
        }
    }

    override fun setDistribution(distribution: Distribution) {
        val userContext = distribution.getUserContext()
        this.kubeAPI = userContext["kubeAPI"] as KubeAPI
        this.cloudAPI = userContext["cloudAPI"] as CloudAPI
        val distributedRepositories =
            userContext["distributedRepositories"] as DistributedRepositories
        this.distributedCloudProcessRepository = distributedRepositories.cloudProcessRepository
    }

}