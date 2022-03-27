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

package app.simplecloud.simplecloud.api.service

import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import app.simplecloud.simplecloud.api.request.group.CloudProcessGroupCreateRequest
import app.simplecloud.simplecloud.api.request.group.CloudProcessGroupDeleteRequest
import app.simplecloud.simplecloud.api.request.group.update.CloudProcessGroupUpdateRequest
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 02.04.2021
 * Time: 12:28
 * @author Frederick Baier
 */
interface CloudProcessGroupService : Service {

    /**
     * Returns the group found by [name] or the futures fails with [NoSuchElementException]
     */
    fun findByName(name: String): CompletableFuture<CloudProcessGroup>

    /**
     * Returns all groups
     */
    fun findAll(): CompletableFuture<List<CloudProcessGroup>>

    /**
     * Creates a request to create a new group
     */
    fun createCreateRequest(configuration: AbstractCloudProcessGroupConfiguration): CloudProcessGroupCreateRequest

    /**
     * Creates a request to update an existing group
     * The returned request type depends on the type of the [group]
     * @see [CloudProcessGroup.createUpdateRequest]
     */
    fun createUpdateRequest(group: CloudProcessGroup): CloudProcessGroupUpdateRequest

    /**
     * Creates a request to delete an existing group
     */
    fun createDeleteRequest(group: CloudProcessGroup): CloudProcessGroupDeleteRequest

}