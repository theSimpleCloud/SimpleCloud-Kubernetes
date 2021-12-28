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

package eu.thesimplecloud.simplecloud.plugin.startup

import com.google.inject.Guice
import eu.thesimplecloud.simplecloud.api.impl.guice.CloudAPIBinderModule
import eu.thesimplecloud.simplecloud.api.impl.util.ClusterKey
import eu.thesimplecloud.simplecloud.api.utils.Address
import eu.thesimplecloud.simplecloud.ignite.bootstrap.IgniteBuilder
import org.apache.ignite.Ignite
import org.apache.ignite.plugin.security.SecurityCredentials

class CloudPlugin {

    fun onEnable() {
        val ignite = startIgnite()
        Guice.createInjector(
            //CloudAPIBinderModule(
            //    ignite,
            //)
        )
    }

    private fun startIgnite(): Ignite {
        val clusterKeyArray = System.getenv("CLUSTER_KEY").split(":")
        val clusterKey = ClusterKey(clusterKeyArray[0], clusterKeyArray[1])
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

}