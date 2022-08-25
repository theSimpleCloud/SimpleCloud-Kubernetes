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

import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.request.process.ProcessExecuteCommandRequest
import app.simplecloud.simplecloud.api.request.process.ProcessShutdownRequest
import app.simplecloud.simplecloud.api.request.process.ProcessStartRequest
import app.simplecloud.simplecloud.api.request.process.ProcessUpdateRequest
import app.simplecloud.simplecloud.api.template.ProcessTemplate
import app.simplecloud.simplecloud.api.template.group.CloudProcessGroup
import app.simplecloud.simplecloud.distribution.api.DistributionComponent
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
     * Returns the process found by the specified [component]
     */
    fun findByDistributionComponent(component: DistributionComponent): CompletableFuture<CloudProcess>

    /**
     * Creates a request to start a process
     */
    fun createStartRequest(template: ProcessTemplate): ProcessStartRequest

    /**
     * Creates a request to stop a process
     */
    fun createShutdownRequest(process: CloudProcess): ProcessShutdownRequest

    /**
     * Creates a request to update a process
     */
    fun createUpdateRequest(process: CloudProcess): ProcessUpdateRequest

    /**
     * Creates a request to execute a command on a process
     */
    fun createExecuteCommandRequest(cloudProcess: CloudProcess, command: String): ProcessExecuteCommandRequest

    /**
     * Returns all registered processes
     */
    fun findAll(): CompletableFuture<List<CloudProcess>>

    /**
     * Returns the logs of the process
     */
    fun getLogs(process: CloudProcess): CompletableFuture<List<String>>

}