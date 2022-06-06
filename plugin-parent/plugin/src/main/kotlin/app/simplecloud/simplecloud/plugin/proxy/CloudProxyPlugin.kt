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

package app.simplecloud.simplecloud.plugin.proxy

import app.simplecloud.simplecloud.api.impl.env.EnvironmentVariables
import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.distribution.api.DistributionFactory
import app.simplecloud.simplecloud.eventapi.EventRegisterer
import app.simplecloud.simplecloud.plugin.OnlineCountUpdaterImpl
import app.simplecloud.simplecloud.plugin.SelfOnlineCountProvider
import app.simplecloud.simplecloud.plugin.startup.CloudPlugin
import app.simplecloud.simplecloud.plugin.startup.SelfProcessProvider

/**
 * Date: 28.05.22
 * Time: 21:24
 * @author Frederick Baier
 *
 */
class CloudProxyPlugin(
    distributionFactory: DistributionFactory,
    environmentVariables: EnvironmentVariables,
    nodeAddress: Address,
    private val proxyServerRegistry: ProxyServerRegistry,
    private val selfOnlineCountProvider: SelfOnlineCountProvider
) {

    private val cloudPlugin = CloudPlugin(distributionFactory, environmentVariables, nodeAddress)
    val cloudAPI = cloudPlugin.pluginCloudAPI
    val proxyController: ProxyController

    init {
        val proxyProcessRegisterer = ProxyProcessRegisterer(this.cloudAPI.getProcessService(), this.proxyServerRegistry)
        proxyProcessRegisterer.registerExistingProcesses()

        this.cloudAPI.getEventManager().registerListener(
            object : EventRegisterer {},
            ProxyCloudListener(this.proxyServerRegistry)
        )
        val selfProcessProvider = SelfProcessProvider(this.cloudPlugin.selfProcessId, this.cloudAPI.getProcessService())
        this.proxyController = ProxyControllerImpl(
            this.cloudAPI.internalPlayerService,
            this.cloudAPI.getProcessService(),
            this.cloudAPI.getProcessGroupService(),
            OnlineCountUpdaterImpl(selfProcessProvider, this.selfOnlineCountProvider),
            this.cloudAPI.getLocalNetworkComponentName()
        )
    }

}