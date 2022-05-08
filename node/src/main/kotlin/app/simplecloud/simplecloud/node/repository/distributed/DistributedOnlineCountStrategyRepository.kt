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

package app.simplecloud.simplecloud.node.repository.distributed

import app.simplecloud.simplecloud.api.impl.repository.distributed.AbstractDistributedRepository
import app.simplecloud.simplecloud.api.process.onlinestrategy.configuration.ProcessOnlineCountStrategyConfiguration
import app.simplecloud.simplecloud.distribution.api.Distribution
import app.simplecloud.simplecloud.node.repository.distributed.predicate.OnlineCountCompareTargetGroupPredicate
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 21.04.2021
 * Time: 21:21
 * @author Frederick Baier
 *
 */
class DistributedOnlineCountStrategyRepository(
    distribution: Distribution
) : AbstractDistributedRepository<String, ProcessOnlineCountStrategyConfiguration>(
    distribution.getOrCreateCache("cloud-online-strategy")
) {

    /**
     * Returns the process group names that are using the specified strategy
     */
    fun findByTargetProcessGroup(groupName: String): CompletableFuture<Collection<ProcessOnlineCountStrategyConfiguration>> {
        return executeQuery(OnlineCountCompareTargetGroupPredicate(groupName))
    }

}