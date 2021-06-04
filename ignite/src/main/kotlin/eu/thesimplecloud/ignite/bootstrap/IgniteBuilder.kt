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

package eu.thesimplecloud.ignite.bootstrap

import eu.thesimplecloud.api.utils.Address
import org.apache.ignite.Ignite
import org.apache.ignite.Ignition
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.events.Event
import org.apache.ignite.lang.IgnitePredicate
import org.apache.ignite.lifecycle.LifecycleBean
import org.apache.ignite.spi.discovery.tcp.ipfinder.TcpDiscoveryIpFinder
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder
import java.util.concurrent.CopyOnWriteArrayList
import eu.thesimplecloud.ignite.bootstrap.security.CustomTcpDiscoverySpi
import eu.thesimplecloud.ignite.bootstrap.security.SecurityPluginConfiguration
import eu.thesimplecloud.ignite.bootstrap.security.SecurityPluginProvider

import org.apache.ignite.plugin.security.SecurityCredentials
import java.io.File


/**
 * Created by IntelliJ IDEA.
 * Date: 31.05.2021
 * Time: 12:02
 * @author Frederick Baier
 */
class IgniteBuilder(
    private val selfHost: Address,
    private val clientMode: Boolean,
    private val credentials: SecurityCredentials,
    private val workingDir: File = File("ignite-working")
) {

    @Volatile
    private var connectAddresses: List<Address> = emptyList()

    @Volatile
    private var lifecycleBean: LifecycleBean? = null

    private val events = CopyOnWriteArrayList<IIgniteEventListener>()

    fun withAddressesToConnectTo(vararg addresses: Address): IgniteBuilder {
        this.connectAddresses = addresses.toList()
        return this
    }

    fun withLifecycleBean(lifecycleBean: LifecycleBean): IgniteBuilder {
        this.lifecycleBean = lifecycleBean
        return this
    }

    fun addEventListener(eventListener: IIgniteEventListener): IgniteBuilder {
        this.events.add(eventListener)
        return this
    }


    fun start(): Ignite {
        val configuration = IgniteConfiguration()
        val securityPluginConfiguration = SecurityPluginConfiguration(credentials)
        configuration.setPluginProviders(SecurityPluginProvider(securityPluginConfiguration))
        configuration.discoverySpi = CustomTcpDiscoverySpi()
            .setSecurityCredentials(credentials)
            .setLocalPort(selfHost.port)
            .setIpFinder(createIpFinder())
        configuration.isClientMode = this.clientMode
        configuration.workDirectory = this.workingDir.absolutePath
        this.lifecycleBean?.let { configuration.setLifecycleBeans(it) }
        val ignite: Ignite = Ignition.start(configuration)

        addEventsToIgnite(ignite)
        return ignite
    }

    private fun createIpFinder(): TcpDiscoveryIpFinder {
        val ipFinder = TcpDiscoveryVmIpFinder()
        ipFinder.setAddresses(getAllAddressesAsString())
        return ipFinder
    }

    private fun addEventsToIgnite(ignite: Ignite) {
        this.events.forEach {
            registerListener(ignite, it)
        }
    }

    private fun registerListener(ignite: Ignite, listenerData: IIgniteEventListener) {
        val predicate = IgnitePredicate<Event> {
            listenerData.onEvent(it)
            return@IgnitePredicate true
        }
        ignite.events().enableLocal(listenerData.getEventType())
        ignite.events().localListen(predicate, listenerData.getEventType())
    }

    private fun getAllAddressesAsString(): Collection<String> {
        val connectAddressesAsString = this.connectAddresses.map { it.asIpString() }
        return connectAddressesAsString.union(listOf(selfHost.asIpString()))
    }

}