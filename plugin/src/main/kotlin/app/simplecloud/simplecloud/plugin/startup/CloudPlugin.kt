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

package app.simplecloud.simplecloud.plugin.startup

import app.simplecloud.simplecloud.api.impl.guice.CloudAPIBinderModule
import app.simplecloud.simplecloud.api.impl.util.ClusterKey
import app.simplecloud.simplecloud.api.impl.util.SingleInstanceBinderModule
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.utils.Address
import app.simplecloud.simplecloud.ignite.bootstrap.IgniteBuilder
import app.simplecloud.simplecloud.plugin.startup.service.*
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import org.apache.ignite.Ignite
import org.apache.ignite.plugin.security.SecurityCredentials

class CloudPlugin(
    private val guiceModule: AbstractModule
) {

    val injector: Injector

    init {
        val ignite = startIgnite()
        val intermediateInjector = Guice.createInjector(
            CloudAPIBinderModule(
                ignite,
                NodeServiceImpl::class.java,
                CloudProcessServiceImpl::class.java,
                CloudProcessGroupServiceImpl::class.java,
                CloudPlayerServiceImpl::class.java,
                PermissionGroupServiceImpl::class.java
            )
        )
        val selfProcess = intermediateInjector.getInstance(SelfProcessGetter::class.java).getSelfProcess()
        this.injector = intermediateInjector.createChildInjector(
            SingleInstanceBinderModule(CloudProcess::class.java, selfProcess),
            this.guiceModule
        )
        this.injector.getInstance(SelfIgniteProcessUpdater::class.java).updateProcessInIgniteBlocking()
    }

    private fun startIgnite(): Ignite {
        val clusterKey = getClusterKey()
        val nodeAddress = Address.fromIpString("ignite:1670")
        val selfAddress = Address.fromIpString("127.0.0.1:1670")
        val igniteBuilder = IgniteBuilder(
            selfAddress,
            true,
            SecurityCredentials(clusterKey.login, clusterKey.password)
        )
        igniteBuilder.withAddressesToConnectTo(nodeAddress)
        return igniteBuilder.start()
    }

    private fun getClusterKey(): ClusterKey {
        val clusterKeyArray = System.getenv("CLUSTER_KEY").split(":")
        return ClusterKey(clusterKeyArray[0], clusterKeyArray[1])
    }

}