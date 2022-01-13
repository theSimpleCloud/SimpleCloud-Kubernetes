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

package app.simplecloud.simplecloud.plugin.startup

import app.simplecloud.simplecloud.api.impl.guice.CloudAPIBinderModule
import app.simplecloud.simplecloud.api.impl.util.ClusterKey
import app.simplecloud.simplecloud.api.utils.Address
import app.simplecloud.simplecloud.ignite.bootstrap.IgniteBuilder
import app.simplecloud.simplecloud.plugin.startup.service.CloudPlayerServiceImpl
import app.simplecloud.simplecloud.plugin.startup.service.CloudProcessGroupServiceImpl
import app.simplecloud.simplecloud.plugin.startup.service.CloudProcessServiceImpl
import app.simplecloud.simplecloud.plugin.startup.service.NodeServiceImpl
import com.google.inject.Guice
import org.apache.ignite.Ignite
import org.apache.ignite.plugin.security.SecurityCredentials

class CloudPlugin {

    fun onEnable() {
        val ignite = startIgnite()
        val injector = Guice.createInjector(
            CloudAPIBinderModule(
                ignite,
                NodeServiceImpl::class.java,
                CloudProcessServiceImpl::class.java,
                CloudProcessGroupServiceImpl::class.java,
                CloudPlayerServiceImpl::class.java
            )
        )
        injector.getInstance(SelfIgniteProcessUpdater::class.java).updateProcessInIgniteBlocking()
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