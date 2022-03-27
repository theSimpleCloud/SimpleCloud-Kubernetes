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

import app.simplecloud.simplecloud.api.impl.repository.ignite.IgniteCloudProcessRepository
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.service.CloudProcessService
import com.google.inject.Inject
import org.apache.ignite.Ignite
import org.apache.ignite.events.EventType

/**
 * Date: 24.01.22
 * Time: 19:26
 * @author Frederick Baier
 *
 */
class ProxyProcessRegisterer @Inject constructor(
    private val cloudProcessService: CloudProcessService,
    private val proxyServerRegistry: ProxyServerRegistry,
    private val processRepository: IgniteCloudProcessRepository,
    //private val igniteUpdateListener: ProxyIgniteUpdateListener,
    private val ignite: Ignite
) {

    fun registerExistingProcesses() {
        val completableFuture = this.cloudProcessService.findAll()
        completableFuture.thenAccept { list-> list.forEach { registerProcess(it) } }
    }

    fun registerIgniteListener() {
        println("Local listen started")
        this.ignite.events().localListen({ event ->
            println("local listen: " + event.message())
            return@localListen false
        }, EventType.EVT_CACHE_OBJECT_PUT)
    }

    private fun registerProcess(cloudProcess: CloudProcess) {
        println("Proxy found process ${cloudProcess.getName()}")
        this.proxyServerRegistry.registerProcess(cloudProcess)
    }



}