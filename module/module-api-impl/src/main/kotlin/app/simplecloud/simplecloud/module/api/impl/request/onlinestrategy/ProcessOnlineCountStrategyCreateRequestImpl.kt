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
import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.api.process.onlinestrategy.ProcessesOnlineCountStrategy
import app.simplecloud.simplecloud.api.process.onlinestrategy.configuration.ProcessOnlineCountStrategyConfiguration
import app.simplecloud.simplecloud.api.utils.DefaultNameRequirement
import app.simplecloud.simplecloud.module.api.internal.InternalNodeProcessOnlineCountStrategyService
import app.simplecloud.simplecloud.module.api.request.onlinestrategy.ProcessOnlineCountStrategyCreateRequest
import java.util.concurrent.CompletableFuture

/**
 * Date: 27.03.22
 * Time: 10:08
 * @author Frederick Baier
 *
 */
class ProcessOnlineCountStrategyCreateRequestImpl(
    private val configuration: ProcessOnlineCountStrategyConfiguration,
    private val internalService: InternalNodeProcessOnlineCountStrategyService,
) : ProcessOnlineCountStrategyCreateRequest {

    override fun submit(): CompletableFuture<ProcessesOnlineCountStrategy> = CloudScope.future {
        DefaultNameRequirement.checkName(configuration.name)
        if (doesStrategyExist(configuration.name)) {
            throw IllegalArgumentException("Strategy already exists")
        }
        if (!doesClassExist(configuration.className)) {
            throw IllegalArgumentException("Class '${configuration.className}' does not exist")
        }
        return@future internalService.createStrategyInternal(configuration)
    }

    private fun doesClassExist(className: String): Boolean {
        return try {
            Class.forName(className)
            true
        } catch (ex: ClassNotFoundException) {
            false
        }
    }

    private suspend fun doesStrategyExist(strategyName: String): Boolean {
        return try {
            this.internalService.findByName(strategyName).await()
            true
        } catch (e: NoSuchElementException) {
            false
        }
    }
}