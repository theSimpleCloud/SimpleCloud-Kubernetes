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

import app.simplecloud.simplecloud.api.impl.repository.ignite.message.CacheAction
import app.simplecloud.simplecloud.api.impl.repository.ignite.message.IgniteCacheUpdateMessaging
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.service.CloudProcessService
import com.google.inject.Inject

/**
 * Date: 24.01.22
 * Time: 19:26
 * @author Frederick Baier
 *
 */
class ProxyProcessRegisterer @Inject constructor(
    private val cloudProcessService: CloudProcessService,
    private val proxyServerRegistry: ProxyServerRegistry,
    private val cacheUpdateMessaging: IgniteCacheUpdateMessaging,
) {

    fun registerExistingProcesses() {
        val completableFuture = this.cloudProcessService.findAll()
        completableFuture.thenAccept { list -> list.forEach { registerProcess(it) } }
    }

    fun registerIgniteListener() {
        this.cacheUpdateMessaging.registerListener(
            "cloud-processes",
            object : IgniteCacheUpdateMessaging.Listener<String> {
                override fun messageReceived(action: CacheAction, key: String) {
                    handleUpdateMessage(action, key)
                }
            }
        )
    }

    private fun handleUpdateMessage(cacheAction: CacheAction, processName: String) {
        when (cacheAction) {
            CacheAction.UPDATE -> registerProcessByName(processName)
            CacheAction.DELETE -> this.proxyServerRegistry.unregisterServer(processName)
        }
    }

    private fun registerProcessByName(processName: String) {
        val processFuture = this.cloudProcessService.findByName(processName)
        processFuture.thenApply { registerProcess(it) }
    }

    private fun registerProcess(cloudProcess: CloudProcess) {
        println("Proxy found process ${cloudProcess.getName()}")
        this.proxyServerRegistry.registerProcess(cloudProcess)
    }

}