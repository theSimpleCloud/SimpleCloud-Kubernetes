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

package eu.thesimplecloud.api.impl.ignite.bootstrap

import eu.thesimplecloud.api.utils.Address
import org.apache.ignite.Ignite
import org.apache.ignite.Ignition
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder

/**
 * Created by IntelliJ IDEA.
 * Date: 31.05.2021
 * Time: 12:02
 * @author Frederick Baier
 */
class IgniteBootstrap(
    private val selfHost: Address,
    private val clientMode: Boolean,
    private val connectAddresses: List<Address>
) {


    fun start(): Ignite {
        val configuration = IgniteConfiguration()
        configuration.isClientMode = this.clientMode
        val ipFinder = TcpDiscoveryVmIpFinder()
        ipFinder.setAddresses(getAllAddressesAsString())
        configuration.discoverySpi = TcpDiscoverySpi().setLocalPort(selfHost.port).setIpFinder(ipFinder)
        return Ignition.start(configuration)
    }

    private fun getAllAddressesAsString(): Collection<String> {
        val connectAddressesAsString = this.connectAddresses.map { it.asIpString() }
        return connectAddressesAsString.union(listOf(selfHost.asIpString()))
    }

}