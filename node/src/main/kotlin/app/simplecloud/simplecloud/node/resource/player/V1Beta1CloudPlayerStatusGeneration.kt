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

package app.simplecloud.simplecloud.node.resource.player

import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedCloudPlayerRepository
import app.simplecloud.simplecloud.module.api.resourcedefinition.StatusGenerationFunction
import java.util.*

/**
 * Date: 10.03.23
 * Time: 18:07
 * @author Frederick Baier
 *
 */
class V1Beta1CloudPlayerStatusGeneration(
    private val distributedCloudPlayerRepository: DistributedCloudPlayerRepository,
) : StatusGenerationFunction<V1Beta1CloudPlayerSpec> {

    override fun generateStatus(resourceName: String, spec: V1Beta1CloudPlayerSpec): V1Beta1CloudPlayerStatus {
        val playerUniqueId = UUID.fromString(resourceName)
        val cloudPlayerConfiguration = this.distributedCloudPlayerRepository.findOrNull(playerUniqueId).join()
        if (cloudPlayerConfiguration == null) {
            return V1Beta1CloudPlayerStatus(false, null, null)
        }
        return V1Beta1CloudPlayerStatus(
            true,
            cloudPlayerConfiguration.connectedServerName,
            cloudPlayerConfiguration.connectedProxyName
        )
    }

}