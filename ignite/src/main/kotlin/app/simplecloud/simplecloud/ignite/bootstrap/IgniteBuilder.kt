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

package app.simplecloud.simplecloud.ignite.bootstrap

import app.simplecloud.simplecloud.api.utils.Address
import app.simplecloud.simplecloud.ignite.bootstrap.security.CustomTcpDiscoverySpi
import org.apache.ignite.Ignite
import org.apache.ignite.Ignition
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.events.Event
import org.apache.ignite.lang.IgnitePredicate
import org.apache.ignite.lifecycle.LifecycleBean
import org.apache.ignite.plugin.security.SecurityCredentials
import org.apache.ignite.spi.discovery.tcp.ipfinder.TcpDiscoveryIpFinder
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList


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
        //val securityPluginConfiguration = SecurityPluginConfiguration(credentials)
        //y.setPluginProviders(SecurityPluginProvider(securityPluginConfiguration))
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