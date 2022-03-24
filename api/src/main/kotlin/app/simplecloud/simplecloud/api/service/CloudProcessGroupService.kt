/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
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