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

package app.simplecloud.simplecloud.node.mongo.group

import app.simplecloud.simplecloud.api.process.group.ProcessGroupType
import app.simplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import app.simplecloud.simplecloud.api.process.group.configuration.CloudLobbyProcessGroupConfiguration
import app.simplecloud.simplecloud.api.process.group.configuration.CloudProxyProcessGroupConfiguration
import app.simplecloud.simplecloud.api.process.group.configuration.CloudServerProcessGroupConfiguration
import dev.morphia.annotations.Entity
import dev.morphia.annotations.Id

@Entity("groups")
class CombinedProcessGroupEntity(
    @Id
    val name: String,
    val maxMemory: Int,
    val maxPlayers: Int,
    val maintenance: Boolean,
    val imageName: String?,
    val onlineCountConfigurationName: String,
    val static: Boolean,
    val stateUpdating: Boolean,
    val startPriority: Int,
    val joinPermission: String?,
    val type: ProcessGroupType,
    val lobbyPriority: Int = -1,
    val startPort: Int = -1
) {

    private constructor() : this(
        "<>",
        1,
        1,
        false,
        "",
        "",
        false,
        false,
        1,
        "",
        ProcessGroupType.PROXY
    )

    fun toConfiguration(): AbstractCloudProcessGroupConfiguration {
        when (type) {
            ProcessGroupType.LOBBY -> {
                return CloudLobbyProcessGroupConfiguration(
                    this.name,
                    this.maxMemory,
                    this.maxPlayers,
                    this.maintenance,
                    this.imageName,
                    this.onlineCountConfigurationName,
                    this.static,
                    this.stateUpdating,
                    this.startPriority,
                    this.joinPermission,
                    this.lobbyPriority
                )
            }
            ProcessGroupType.PROXY -> {
                return CloudProxyProcessGroupConfiguration(
                    this.name,
                    this.maxMemory,
                    this.maxPlayers,
                    this.maintenance,
                    this.imageName,
                    this.onlineCountConfigurationName,
                    this.static,
                    this.stateUpdating,
                    this.startPriority,
                    this.joinPermission,
                    this.startPort
                )
            }
            ProcessGroupType.SERVER -> {
                return CloudServerProcessGroupConfiguration(
                    this.name,
                    this.maxMemory,
                    this.maxPlayers,
                    this.maintenance,
                    this.imageName,
                    this.onlineCountConfigurationName,
                    this.static,
                    this.stateUpdating,
                    this.startPriority,
                    this.joinPermission,
                )
            }
        }
    }

    companion object {
        fun fromGroupConfiguration(configuration: AbstractCloudProcessGroupConfiguration): CombinedProcessGroupEntity {
            val startPort = if (configuration is CloudProxyProcessGroupConfiguration) configuration.startPort else -1
            val lobbyPriority =
                if (configuration is CloudLobbyProcessGroupConfiguration) configuration.lobbyPriority else -1
            return CombinedProcessGroupEntity(
                configuration.name,
                configuration.maxMemory,
                configuration.maxPlayers,
                configuration.maintenance,
                configuration.imageName,
                configuration.onlineCountConfigurationName,
                configuration.static,
                configuration.stateUpdating,
                configuration.startPriority,
                configuration.joinPermission,
                configuration.type,
                lobbyPriority,
                startPort
            )
        }
    }

}