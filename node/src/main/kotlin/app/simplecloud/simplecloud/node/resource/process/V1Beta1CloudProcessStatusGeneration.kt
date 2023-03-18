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

package app.simplecloud.simplecloud.node.resource.process

import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedCloudProcessRepository
import app.simplecloud.simplecloud.module.api.resourcedefinition.StatusGenerationFunction

/**
 * Date: 16.03.23
 * Time: 08:53
 * @author Frederick Baier
 *
 */
class V1Beta1CloudProcessStatusGeneration(
    private val distributedRepository: DistributedCloudProcessRepository,
) : StatusGenerationFunction<V1Beta1CloudProcessSpec> {

    override fun generateStatus(resourceName: String, spec: V1Beta1CloudProcessSpec): V1Beta1CloudProcessStatus? {
        val processConfiguration = this.distributedRepository.findOrNull(resourceName).join() ?: return null

        return V1Beta1CloudProcessStatus(
            processConfiguration.uniqueId,
            processConfiguration.processNumber,
            processConfiguration.state,
            processConfiguration.visible,
            processConfiguration.usedMemory,
            processConfiguration.onlinePlayers,
            processConfiguration.static,
            processConfiguration.processTemplateType
        )
    }

}