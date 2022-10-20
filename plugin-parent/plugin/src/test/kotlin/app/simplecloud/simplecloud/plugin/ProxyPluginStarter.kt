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
import app.simplecloud.simplecloud.plugin.proxy.CloudPlayerTestActions
import app.simplecloud.simplecloud.plugin.proxy.CloudProxyPlugin
import app.simplecloud.simplecloud.plugin.proxy.TestProxyServerRegistry
import app.simplecloud.simplecloud.plugin.proxy.TestSelfOnlineCountProvider

class ProxyPluginStarter(
    private val environmentVariables: EnvironmentVariables,
    private val translatedAddress: Address
) {

    private val proxyServerRegistry = TestProxyServerRegistry()
    private val selfOnlineCountProvider = TestSelfOnlineCountProvider()


    fun startPlugin(): ProxyPluginConfig {
        val cloudProxyPlugin = CloudProxyPlugin(
            TestDistributionFactoryImpl(),
            this.environmentVariables,
            this.translatedAddress,
            this.proxyServerRegistry,
            this.selfOnlineCountProvider,
            CloudPlayerTestActions()
        )
        return ProxyPluginConfig(cloudProxyPlugin, selfOnlineCountProvider)
    }

    data class ProxyPluginConfig(
        val plugin: CloudProxyPlugin,
        val testSelfOnlineCountProvider: TestSelfOnlineCountProvider
    )


}
