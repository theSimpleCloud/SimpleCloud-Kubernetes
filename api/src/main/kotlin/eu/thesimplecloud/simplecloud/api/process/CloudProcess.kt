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

package eu.thesimplecloud.simplecloud.api.process

import eu.thesimplecloud.simplecloud.api.jvmargs.JVMArguments
import eu.thesimplecloud.simplecloud.api.node.Node
import eu.thesimplecloud.simplecloud.api.process.group.CloudProcessGroup
import eu.thesimplecloud.simplecloud.api.process.group.ProcessGroupType
import eu.thesimplecloud.simplecloud.api.process.state.ProcessState
import eu.thesimplecloud.simplecloud.api.template.Template
import eu.thesimplecloud.simplecloud.api.process.version.ProcessVersion
import eu.thesimplecloud.simplecloud.api.request.process.ProcessShutdownRequest
import eu.thesimplecloud.simplecloud.api.utils.Identifiable
import eu.thesimplecloud.simplecloud.api.utils.Address
import eu.thesimplecloud.simplecloud.api.utils.NetworkComponent
import java.util.*
import java.util.concurrent.CompletableFuture

interface CloudProcess : NetworkComponent, Identifiable<String> {

    /**
     * Returns the unique id of the process
     */
    fun getUniqueId(): UUID

    /**
     * Returns the group name of this process
     */
    fun getGroupName(): String

    /**
     * Returns the group of the process
     */
    fun getGroup(): CompletableFuture<CloudProcessGroup>

    /**
     * Returns the process number
     * e.g The name is Lobby-2 -> 2 would be the process number
     */
    fun getProcessNumber(): Int

    /**
     * Returns the state of the process
     */
    fun getState(): ProcessState

    /**
     * Returns the maximum amount of memory this process can use in MB
     */
    fun getMaxMemory(): Int

    /**
     * Returns the amount of memory the process currently uses in MB
     */
    fun getUsedMemory(): Int

    /**
     * Returns the maximum amount of players that can be simultaneously connected the process
     */
    fun getMaxPlayers(): Int

    /**
     * Returns the address of the process
     */
    fun getAddress(): Address

    /**
     * Returns whether the process is static
     */
    fun isStatic(): Boolean

    /**
     * Returns the process type
     */
    fun getProcessType(): ProcessGroupType

    /**
     * Returns the version the process is running with
     */
    fun getVersion(): CompletableFuture<ProcessVersion>

    /**
     * Returns the template this process was started from
     */
    fun getTemplate(): CompletableFuture<Template>

    /**
     * Returns the [JVMArguments] the process used to start
     */
    fun getJvmArguments(): CompletableFuture<JVMArguments>

    /**
     * Returns the node this process is running on
     */
    fun getNodeRunningOn(): CompletableFuture<Node>

    /**
     * Returns the termination future
     * The termination future will be completed when the process was stopped
     * This methods always returns the same [CompletableFuture]
     */
    fun terminationFuture(): CompletableFuture<Void>

    /**
     * Returns the started future
     * The started future will be completed when the process was started
     * This methods always returns the same [CompletableFuture]
     */
    fun startedFuture(): CompletableFuture<Void>

    /**
     * Returns the configuration of this group
     */
    fun toConfiguration(): CloudProcessConfiguration

    /**
     * Creates a request to stop this service
     */
    fun createShutdownRequest(): ProcessShutdownRequest

}