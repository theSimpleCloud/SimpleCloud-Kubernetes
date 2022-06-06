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

package app.simplecloud.simplecloud.plugin.server

import app.simplecloud.simplecloud.api.impl.env.EnvironmentVariables
import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.distribution.api.DistributionFactory
import app.simplecloud.simplecloud.plugin.OnlineCountUpdater
import app.simplecloud.simplecloud.plugin.OnlineCountUpdaterImpl
import app.simplecloud.simplecloud.plugin.SelfOnlineCountProvider
import app.simplecloud.simplecloud.plugin.startup.CloudPlugin
import app.simplecloud.simplecloud.plugin.startup.SelfProcessProvider

/**
 * Date: 03.06.22
 * Time: 19:55
 * @author Frederick Baier
 *
 */
class CloudServerPlugin(
    distributionFactory: DistributionFactory,
    environmentVariables: EnvironmentVariables,
    nodeAddress: Address,
    private val selfOnlineCountProvider: SelfOnlineCountProvider
) {

    private val cloudPlugin = CloudPlugin(distributionFactory, environmentVariables, nodeAddress)
    val cloudAPI = cloudPlugin.pluginCloudAPI
    val onlineCountUpdater: OnlineCountUpdater

    init {
        val selfProcessProvider = SelfProcessProvider(this.cloudPlugin.selfProcessId, this.cloudAPI.getProcessService())
        this.onlineCountUpdater = OnlineCountUpdaterImpl(selfProcessProvider, this.selfOnlineCountProvider)
    }

}