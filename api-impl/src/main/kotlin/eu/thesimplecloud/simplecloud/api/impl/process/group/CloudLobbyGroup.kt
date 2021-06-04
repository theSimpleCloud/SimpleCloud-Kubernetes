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

package eu.thesimplecloud.simplecloud.api.impl.process.group

import eu.thesimplecloud.simplecloud.api.impl.process.request.CloudLobbyGroupUpdateRequest
import eu.thesimplecloud.simplecloud.api.process.ProcessGroupType
import eu.thesimplecloud.simplecloud.api.process.group.lobby.ICloudLobbyGroup
import eu.thesimplecloud.simplecloud.api.process.group.update.ICloudLobbyGroupUpdateRequest
import eu.thesimplecloud.simplecloud.api.process.group.update.ICloudProcessGroupUpdateRequest

/**
 * Created by IntelliJ IDEA.
 * Date: 06.04.2021
 * Time: 09:59
 * @author Frederick Baier
 */
class CloudLobbyGroup(
    name: String,
    maxMemory: Int,
    maxPlayers: Int,
    maintenance: Boolean,
    minimumProcessCount: Int,
    maximumProcessCount: Int,
    templateName: String,
    jvmArgumentName: String?,
    versionName: String,
    onlineCountConfigurationName: String,
    static: Boolean,
    stateUpdating: Boolean,
    startPriority: Int,
    joinPermission: String?,
    nodeNamesAllowedToStartOn: List<String>,
    private val lobbyPriority: Int
) : CloudServerGroup(
    name,
    maxMemory,
    maxPlayers,
    maintenance,
    minimumProcessCount,
    maximumProcessCount,
    templateName,
    jvmArgumentName,
    versionName,
    onlineCountConfigurationName,
    static,
    stateUpdating,
    startPriority,
    joinPermission,
    nodeNamesAllowedToStartOn
), ICloudLobbyGroup {

    override fun getLobbyPriority(): Int {
        return this.lobbyPriority
    }

    override fun createUpdateRequest(): ICloudLobbyGroupUpdateRequest {
        return CloudLobbyGroupUpdateRequest(this)
    }

    override fun getProcessGroupType(): ProcessGroupType {
        return ProcessGroupType.LOBBY
    }

}