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

package eu.thesimplecloud.simplecloud.api.request.group.update

import eu.thesimplecloud.simplecloud.api.image.Image
import eu.thesimplecloud.simplecloud.api.jvmargs.JVMArguments
import eu.thesimplecloud.simplecloud.api.process.group.CloudProcessGroup
import eu.thesimplecloud.simplecloud.api.process.onlineonfiguration.ProcessesOnlineCountConfiguration
import eu.thesimplecloud.simplecloud.api.process.state.ProcessState
import eu.thesimplecloud.simplecloud.api.process.version.ProcessVersion
import eu.thesimplecloud.simplecloud.api.utils.Request
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 17.03.2021
 * Time: 18:59
 * @author Frederick Baier
 *
 * Request for updating a registered group
 *
 */
interface CloudProcessGroupUpdateRequest : Request<CloudProcessGroup> {

    /**
     * Returns the group this request updates
     */
    fun getProcessGroup(): CloudProcessGroup

    /**
     * Sets the maximum amount of memory
     * @return this
     */
    fun setMaxMemory(memory: Int): CloudProcessGroupUpdateRequest

    /**
     * Sets the maximum amount of players
     * @return this
     */
    fun setMaxPlayers(players: Int): CloudProcessGroupUpdateRequest

    /**
     * Sets the version for the group
     * @return this
     */
    fun setVersion(version: ProcessVersion): CloudProcessGroupUpdateRequest

    /**
     * Sets the version for the group
     * @return this
     */
    fun setVersion(versionFuture: CompletableFuture<ProcessVersion>): CloudProcessGroupUpdateRequest

    /**
     * Sets the image for the group
     * @return this
     */
    fun setImage(image: Image?): CloudProcessGroupUpdateRequest

    /**
     * Sets the jvm arguments for the group
     * @return this
     */
    fun setJvmArguments(jvmArguments: JVMArguments?): CloudProcessGroupUpdateRequest

    /**
     * Sets the jvm arguments for the group
     * @return this
     */
    fun setJvmArguments(jvmArgumentsFuture: CompletableFuture<JVMArguments>): CloudProcessGroupUpdateRequest

    /**
     * Sets the online count configuration for the group
     * @return this
     */
    fun setOnlineCountConfiguration(onlineCountConfiguration: ProcessesOnlineCountConfiguration): CloudProcessGroupUpdateRequest

    /**
     * Sets the online count configuration for the group
     * @return this
     */
    fun setOnlineCountConfiguration(onlineCountConfigurationFuture: CompletableFuture<ProcessesOnlineCountConfiguration>): CloudProcessGroupUpdateRequest

    /**
     * Sets the maintenance state for the group
     * @return this
     */
    fun setMaintenance(maintenance: Boolean): CloudProcessGroupUpdateRequest

    /**
     * Sets the minimum count of processes to be VISIBLE
     * @return this
     */
    fun setMinimumOnlineProcessCount(minCount: Int): CloudProcessGroupUpdateRequest

    /**
     * Sets the maximum count of processes to be VISIBLE
     * @return this
     */
    fun setMaximumOnlineProcessCount(maxCount: Int): CloudProcessGroupUpdateRequest

    /**
     * Sets the permission a player need to join processes of the group
     * @return this
     */
    fun setJoinPermission(permission: String?): CloudProcessGroupUpdateRequest

    /**
     * Sets whether the state of processes shall be automatically set to [ProcessState.VISIBLE]
     *  after the process has been started
     * @return this
     */
    fun setStateUpdating(stateUpdating: Boolean): CloudProcessGroupUpdateRequest

    /**
     * Sets start priority for the group (higher will be started first)
     * @return this
     */
    fun setStartPriority(priority: Int): CloudProcessGroupUpdateRequest



}