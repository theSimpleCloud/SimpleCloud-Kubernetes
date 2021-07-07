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

package eu.thesimplecloud.simplecloud.api.impl.utils

import com.google.inject.Singleton
import eu.thesimplecloud.simplecloud.api.impl.process.group.CloudLobbyProcessGroup
import eu.thesimplecloud.simplecloud.api.impl.process.group.CloudProxyProcessGroup
import eu.thesimplecloud.simplecloud.api.impl.process.group.CloudServerProcessGroup
import eu.thesimplecloud.simplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import eu.thesimplecloud.simplecloud.api.process.group.configuration.CloudLobbyProcessGroupConfiguration
import eu.thesimplecloud.simplecloud.api.process.group.configuration.CloudProxyProcessGroupConfiguration
import eu.thesimplecloud.simplecloud.api.process.group.configuration.CloudServerProcessGroupConfiguration

/**
 * Created by IntelliJ IDEA.
 * Date: 02/07/2021
 * Time: 11:03
 * @author Frederick Baier
 */
@Singleton
class CloudProcessGroupFactory {

    fun createGroup(configuration: AbstractCloudProcessGroupConfiguration): ICloudProcessGroup {
        return when (configuration) {
            is CloudLobbyProcessGroupConfiguration -> {
                CloudLobbyProcessGroup(
                    configuration.name,
                    configuration.maxMemory,
                    configuration.maxPlayers,
                    configuration.maintenance,
                    configuration.minimumProcessCount,
                    configuration.maximumProcessCount,
                    configuration.templateName,
                    configuration.jvmArgumentName,
                    configuration.versionName,
                    configuration.onlineCountConfigurationName,
                    configuration.static,
                    configuration.stateUpdating,
                    configuration.startPriority,
                    configuration.joinPermission,
                    configuration.nodeNamesAllowedToStartOn,
                    configuration.lobbyPriority
                )
            }
            is CloudProxyProcessGroupConfiguration -> {
                CloudProxyProcessGroup(
                    configuration.name,
                    configuration.maxMemory,
                    configuration.maxPlayers,
                    configuration.maintenance,
                    configuration.minimumProcessCount,
                    configuration.maximumProcessCount,
                    configuration.templateName,
                    configuration.jvmArgumentName,
                    configuration.versionName,
                    configuration.onlineCountConfigurationName,
                    configuration.static,
                    configuration.stateUpdating,
                    configuration.startPriority,
                    configuration.joinPermission,
                    configuration.nodeNamesAllowedToStartOn,
                    configuration.startPort
                )
            }
            is CloudServerProcessGroupConfiguration -> {
                CloudServerProcessGroup(
                    configuration.name,
                    configuration.maxMemory,
                    configuration.maxPlayers,
                    configuration.maintenance,
                    configuration.minimumProcessCount,
                    configuration.maximumProcessCount,
                    configuration.templateName,
                    configuration.jvmArgumentName,
                    configuration.versionName,
                    configuration.onlineCountConfigurationName,
                    configuration.static,
                    configuration.stateUpdating,
                    configuration.startPriority,
                    configuration.joinPermission,
                    configuration.nodeNamesAllowedToStartOn
                )
            }
            else -> throw IllegalArgumentException("Unknown type: ${configuration::class.java.name}")
        }
    }

}