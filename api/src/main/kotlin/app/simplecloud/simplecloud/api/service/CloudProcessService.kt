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

import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.request.process.ProcessShutdownRequest
import app.simplecloud.simplecloud.api.request.process.ProcessStartRequest
import app.simplecloud.simplecloud.api.request.process.ProcessUpdateRequest
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 03.04.2021
 * Time: 19:27
 * @author Frederick Baier
 */
interface CloudProcessService : Service {

    /**
     * Returns the processes found by the specified [name]
     */
    fun findByName(name: String): CompletableFuture<CloudProcess>

    /**
     * Returns the processes found by the specified [names]
     */
    fun findByNames(vararg names: String): CompletableFuture<List<CloudProcess>>

    /**
     * Returns the processes found by the specified [group]
     */
    fun findByGroup(group: CloudProcessGroup): CompletableFuture<List<CloudProcess>>

    /**
     * Returns the processes found by the specified [groupName]
     */
    fun findByGroup(groupName: String): CompletableFuture<List<CloudProcess>>

    /**
     * Returns the process found by the specified [uniqueId]
     */
    fun findByUniqueId(uniqueId: UUID): CompletableFuture<CloudProcess>

    /**
     * Returns the process found by the specified [igniteId]
     */
    fun findByIgniteId(igniteId: UUID): CompletableFuture<CloudProcess>

    /**
     * Creates a request to start a process
     */
    fun createStartRequest(group: CloudProcessGroup): ProcessStartRequest

    /**
     * Creates a request to stop a process
     */
    fun createShutdownRequest(process: CloudProcess): ProcessShutdownRequest

    /**
     * Creates a request to update a process
     */
    fun createUpdateRequest(process: CloudProcess): ProcessUpdateRequest

    /**
     * Returns all registered processes
     */
    fun findAll(): CompletableFuture<List<CloudProcess>>

}