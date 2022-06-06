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

import app.simplecloud.simplecloud.api.impl.env.EnvironmentVariables
import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.distribution.test.TestDistributionFactoryImpl
import app.simplecloud.simplecloud.plugin.proxy.TestSelfOnlineCountProvider
import app.simplecloud.simplecloud.plugin.server.CloudServerPlugin

class ServerPluginStarter(
    private val environmentVariables: EnvironmentVariables,
    private val translatedAddress: Address
) {

    private val selfOnlineCountProvider = TestSelfOnlineCountProvider()

    fun startPlugin(): ServerPluginConfig {
        val cloudProxyPlugin = CloudServerPlugin(
            TestDistributionFactoryImpl(),
            this.environmentVariables,
            this.translatedAddress,
            this.selfOnlineCountProvider
        )
        return ServerPluginConfig(cloudProxyPlugin, selfOnlineCountProvider)
    }

    data class ServerPluginConfig(
        val plugin: CloudServerPlugin,
        val testSelfOnlineCountProvider: TestSelfOnlineCountProvider
    )

}
