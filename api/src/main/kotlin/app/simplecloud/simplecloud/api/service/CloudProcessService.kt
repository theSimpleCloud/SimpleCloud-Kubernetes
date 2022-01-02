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
    fun findProcessByName(name: String): CompletableFuture<CloudProcess>

    /**
     * Returns the processes found by the specified [names]
     */
    fun findProcessesByName(vararg names: String): CompletableFuture<List<CloudProcess>>

    /**
     * Returns the processes found by the specified [group]
     */
    fun findProcessesByGroup(group: CloudProcessGroup): CompletableFuture<List<CloudProcess>>

    /**
     * Returns the processes found by the specified [groupName]
     */
    fun findProcessesByGroup(groupName: String): CompletableFuture<List<CloudProcess>>

    /**
     * Returns the process found by the specified [uniqueId]
     */
    fun findProcessByUniqueId(uniqueId: UUID): CompletableFuture<CloudProcess>

    /**
     * Creates a request to start a process
     */
    fun createProcessStartRequest(group: CloudProcessGroup): ProcessStartRequest

    /**
     * Creates a request to stop a service
     */
    fun createProcessShutdownRequest(group: CloudProcess): ProcessShutdownRequest

    /**
     * Returns all registered processes
     */
    fun findAll(): CompletableFuture<List<CloudProcess>>

}