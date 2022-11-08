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

package app.simplecloud.simplecloud.module.api.impl.request.onlinestrategy

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.api.process.onlinestrategy.ProcessesOnlineCountStrategy
import app.simplecloud.simplecloud.api.process.onlinestrategy.configuration.ProcessOnlineCountStrategyConfiguration
import app.simplecloud.simplecloud.module.api.internal.InternalNodeProcessOnlineCountStrategyService
import app.simplecloud.simplecloud.module.api.request.onlinestrategy.ProcessOnlineCountStrategyUpdateRequest
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Date: 27.03.22
 * Time: 10:08
 * @author Frederick Baier
 *
 */
class ProcessOnlineCountStrategyUpdateRequestImpl(
    private val strategy: ProcessesOnlineCountStrategy,
    private val internalService: InternalNodeProcessOnlineCountStrategyService,
) : ProcessOnlineCountStrategyUpdateRequest {

    private val targetGroupNames = CopyOnWriteArraySet(this.strategy.getTargetGroupNames())


    @Volatile
    private var dataMap = this.strategy.toConfiguration().dataMap

    override fun getStrategy(): ProcessesOnlineCountStrategy {
        return this.strategy
    }

    override fun clearTargetGroups(): ProcessOnlineCountStrategyUpdateRequest {
        this.targetGroupNames.clear()
        return this
    }

    override fun addTargetGroup(name: String): ProcessOnlineCountStrategyUpdateRequest {
        this.targetGroupNames.add(name)
        return this
    }

    override fun removeTargetGroup(name: String): ProcessOnlineCountStrategyUpdateRequest {
        this.targetGroupNames.remove(name)
        return this
    }

    override fun setData(dataMap: Map<String, String>): ProcessOnlineCountStrategyUpdateRequest {
        this.dataMap = dataMap
        return this
    }

    override fun submit(): CompletableFuture<Unit> = CloudScope.future {
        val configuration = ProcessOnlineCountStrategyConfiguration(
            strategy.getName(),
            strategy.toConfiguration().className,
            targetGroupNames,
            dataMap
        )
        internalService.updateStrategyInternal(configuration)
    }
}