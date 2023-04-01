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

package app.simplecloud.simplecloud.module.api.service

import app.simplecloud.simplecloud.api.process.onlinestrategy.ProcessesOnlineCountStrategy
import app.simplecloud.simplecloud.api.process.onlinestrategy.configuration.ProcessOnlineCountStrategyConfiguration
import app.simplecloud.simplecloud.api.template.group.CloudProcessGroup
import app.simplecloud.simplecloud.module.api.request.onlinestrategy.ProcessOnlineCountStrategyCreateRequest
import app.simplecloud.simplecloud.module.api.request.onlinestrategy.ProcessOnlineCountStrategyDeleteRequest
import app.simplecloud.simplecloud.module.api.request.onlinestrategy.ProcessOnlineCountStrategyUpdateRequest
import java.util.concurrent.CompletableFuture

/**
 * Date: 25.03.22
 * Time: 08:43
 * @author Frederick Baier
 *
 */
interface NodeProcessOnlineStrategyService {

    /**
     * Returns the strategy found by the specified [name]
     */
    fun findByName(name: String): CompletableFuture<ProcessesOnlineCountStrategy>

    /**
     * Returns all available strategies
     */
    fun findAll(): CompletableFuture<List<ProcessesOnlineCountStrategy>>

    /**
     * Returns the [ProcessesOnlineCountStrategy] found for the specified [processGroup] or a default config
     */
    fun findByProcessGroup(processGroup: CloudProcessGroup): CompletableFuture<ProcessesOnlineCountStrategy>

    /**
     * Returns a request to create a new [ProcessesOnlineCountStrategy]
     */
    fun createCreateRequest(configuration: ProcessOnlineCountStrategyConfiguration): ProcessOnlineCountStrategyCreateRequest

    /**
     * Returns a request to update a [ProcessesOnlineCountStrategy]
     */
    fun createUpdateRequest(strategy: ProcessesOnlineCountStrategy): ProcessOnlineCountStrategyUpdateRequest

    /**
     * Returns a request to delete the specified [strategy]
     */
    fun createDeleteRequest(strategy: ProcessesOnlineCountStrategy): ProcessOnlineCountStrategyDeleteRequest

}