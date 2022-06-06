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

package app.simplecloud.simplecloud.plugin

import app.simplecloud.simplecloud.api.impl.env.VirtualEnvironmentVariables
import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.kubernetes.api.KubeAPI
import java.util.*

/**
 * Date: 03.06.22
 * Time: 21:13
 * @author Frederick Baier
 *
 */
class TestCloudPluginStarter(
    private val kubeAPI: KubeAPI,
    private val processId: UUID
) {

    private val environmentVariables =
        VirtualEnvironmentVariables(mapOf("SIMPLECLOUD_PROCESS_ID" to processId.toString()))
    private val translatedAddress =
        this.kubeAPI.getNetworkService().translateAddress(Address.fromIpString("distribution:1670"))


    fun createProxyPlugin(): ProxyPluginStarter.ProxyPluginConfig {
        return ProxyPluginStarter(environmentVariables, translatedAddress).startPlugin()
    }

    fun createServerPlugin(): ServerPluginStarter.ServerPluginConfig {
        return ServerPluginStarter(environmentVariables, translatedAddress).startPlugin()
    }

}